package business.venda;

import business.produtos.ComparaPacotesByDesconto;
import business.produtos.Componente;
import business.produtos.Pacote;
import data.ComponenteDAO;
import data.PacoteDAO;
import javafx.util.Pair;

import java.util.*;

public class Configuracao {
	private Set<Componente> componentes; //Componentes da configuração
	private Set<Integer> dependentes; 	 //Id's dos componentes que precisam de ser adicionados
	private Set<Pacote> pacotes;		 //Pacotes da configuração
	private ComponenteDAO cDAO;
	private PacoteDAO pDAO;

	public Configuracao(ComponenteDAO cDAO, PacoteDAO pDAO) {
		this.componentes = new HashSet<>();
		this.dependentes = new HashSet<>();
		this.pacotes = new HashSet<>();
		this.cDAO = cDAO;
		this.pDAO = pDAO;
	}

	/*
	 * Adiciona um componente, tratando as incompatibilidades e dependencias, assim como a formação de um pacote.
	 * @param idComponente id do componente a adicionar
	 * @returns valor a acrescentar à encomenda
	 */
	public float adicionarComponente(int idComponente) throws ComponenteJaExisteNaConfiguracao {
		Componente componente = cDAO.get(idComponente);

		if(componentes.contains(componente)) {
			throw new ComponenteJaExisteNaConfiguracao("Já existe");
		}

		//Manhas para não fazer métodos diferentes. Todos recebem lista
		HashSet<Componente> componentesAdd = new HashSet<>();
		componentesAdd.add(componente);

		//Tratamentos
		float valorRetirado = tratarIncompatibilidades(componentesAdd);
		float valorAcrescentado = tratarDependencias(componentesAdd);

		//Adição do componente
		this.componentes.add(componente);

		//Se foi adicionado um dos dependentes
		boolean escolha = dependentes.contains(idComponente);
		if (escolha) dependentes.remove(idComponente);

		//Formação de pacote
		float descontoAcrescentado = formacaoPacote(idComponente);

		return (valorAcrescentado - valorRetirado - descontoAcrescentado);
	}

	/*
	 *Verifica se houve formou de algum pacote com a adição de um componente
	 *@param idComponente id do componente que poderá ter formado pacote
	 *@returns val valor a descontar para o total da encomenda
	 */
	public float formacaoPacote(int idComponente) {
		HashSet<Pacote> pac;    //Pacotes que tem na sua constituição o componente fornecido como parámetro
		HashSet<Integer> aux;	//Componentes de um pacote
		TreeSet<Pacote> formados = new TreeSet<Pacote>(new ComparaPacotesByDesconto()); // pacotes que podem ser formados
		pac = (HashSet<Pacote>) pDAO.getPacotesComComponente(idComponente);
		boolean flag;			//Irá indicar se pode ser formado o pacote

		//Por cada pacote que na sua constituição tem o componente vamos verificar se temos todos os componentes necessários
		for (Pacote p : pac) {
			aux = (HashSet<Integer>) p.getComponentes();
			flag = true;
			for (int id : aux) {
				for(Componente c: componentes)
					if(id != c.getId())
						flag = false;
			}
			if(flag) formados.add(p);
		}

		//É retirado o pacote com menor custo da estrutura auxiliar e adicionado à configuração
		Pacote p = formados.first();
		pacotes.add(p);

		return p.getDesconto();
	}
	/*
	 *Vai buscar as incompatibilidades dos componentes que recebeu como argumento e remove-as
	 *@param componentes Componentes cujas incompatibilidades serão removidas
	 */
	private float tratarIncompatibilidades(Set<Componente> componentes) {
		HashSet<Integer> idIncompativeis = new HashSet<>();  //Id's dos componentes incompatíveis de todos os componentes
		Set<Integer> aux;									 //Id's dos componentes incompatíveis de cada um dos componentes

		//Vai buscar todas as incompatibilidades dos componentes
		for(Componente c : componentes){
			int id = c.getId();
			aux = c.getIncompatibilidades();
			idIncompativeis.addAll(aux);
		}
		return removerComponentes(idIncompativeis);
	}

	/*
	 *Vais buscar dependencias e adiciona-as a this.dependentes
	 *@param componentes Componentes cujas dependências terão de ser adicionadas
	 *@returns val Valor dos componentes que vão ser adicionados a this.componentes ---- para aproveitar o ciclo
	 */
	private float tratarDependencias(Set<Componente> componentes){
		HashSet<Integer> idDependentes = new HashSet<>(); //Retem os id's dos componentes dependentes do Set fornecido
		HashSet aux = new HashSet();					  //Id's dos componentes dependentes de cada um dos componentes do Set fornecido
		float val = 0;

		//Vai buscar todas as dependências
		for(Componente c : componentes){
			int id = c.getId();
			val += c.getPreco();
			aux = (HashSet) c.getDepedendencias();
			idDependentes.addAll(aux);
		}
		//Se houver adiciona à lista de dependências da configuração
		if (idDependentes.size() != 0) {
			this.dependentes.addAll(aux);
		}
		return val;
	}

	/*
	 *Vai buscar as dependências dos componentes a remover e removias da lista dependentes.
	 *Remove os componentes recebidos
	 *@param idComponentes Componentes a ser removidos
	 *@returns valor a ser diminuido ao valor da encomenda
	 */
	private float removerComponentes (Set<Integer> idComponentes){
		HashSet<Integer> idDependentes = new HashSet<>();  //Dependências de todos os componentes a remover
		ArrayList aux;									   //Dependências de cada um dos componentes a remover
		float valRetirar = 0;                              //valor a retirar da encomenda

		//Vai buscar os dependentes dos Componentes a remover
		for(int id: idComponentes){
			aux=(ArrayList) cDAO.getDependentes(id);
			idDependentes.addAll(aux);
		}
		//Retira os dependentes da config.
		dependentes.removeAll(idDependentes);

		//Retira os componentes da config. e também o seu valor
		for(Componente c : componentes){
			int id = c.getId();
			if(idComponentes.contains(id)){
				valRetirar+=c.getPreco();
				componentes.remove(c);}
		}
		for(Pacote p : pacotes){
			if(idComponentes.contains(p.getComponentes())) pacotes.remove(p);
			valRetirar-=p.getDesconto();
		}

		return valRetirar;
	}
	/*
	 *Remove um componente e as dependencias que gerou
	 *@param idComponente Id componente a ser retirado
	 */
	public float removerComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao {
		//Isto parece-me desnecessário
		Componente c = cDAO.get(idComponente);
		if (!componentes.contains(c)) throw new ComponenteNaoExisteNaConfiguracao("Componentes não existe");

		HashSet<Integer> idComponentes = new HashSet<>();
		idComponentes.add(idComponente);

		return removerComponentes(idComponentes);
	}

	/*
	 *Adiciona um pacote e os seus componentes. Trata dependencias, conflitos de pacotes e incompatibilidades.
	 *@param idPacote Id do pacote a ser adicionado
	 *@returns par em que a chave é a quantia a ser descontada na encomenda e o valor é a lista de pacotes que podem ter sido removidos
	 */
	public float adicionarPacote(int idPacote) throws PacoteJaExisteNaConfiguracao, PacoteGeraConflitos {
		Pacote p = pDAO.get(idPacote);

		if(pacotes.contains(p)) {
			throw new PacoteJaExisteNaConfiguracao("Já existe");
		}

		HashSet<Componente> componentes = (HashSet<Componente>)pDAO.getComponentesPacote(idPacote);

		if(existeConflito(componentes)) {
			throw new PacoteGeraConflitos("Já existe um pacote com algum dos componentes do que pretende adicionar");
		}

		//tratamentos
		float valorRetirado = tratarIncompatibilidades(componentes);
		float valorAcrescentado = tratarDependencias(componentes);
		float descontoAcrescentado = p.getDesconto();

		return valorAcrescentado - valorRetirado - descontoAcrescentado;
	}

	private boolean existeConflito(Set<Componente> componentes) {
		boolean found = false;
		Iterator<Pacote> it = pacotes.iterator();
		Set<Integer> compIds;

		while (it.hasNext() && !found) {
			compIds = it.next().getComponentes();
			for (Componente c : componentes) {
				int idC = c.getId();
				found = compIds.contains(idC);
			}
		}
		return found;
	}
	public float removerPacote(int idPacote) throws PacoteNaoExisteNaConfiguracao {
		Pacote p = pDAO.get(idPacote);
		if(!pacotes.contains(p)) throw new PacoteNaoExisteNaConfiguracao("Pacote não existe");
		return removerComponentes(p.getComponentes());

	}
	public Pair<Set<Integer>,Set<Integer>> getEfeitosSecundariosAdicionarComponente(int idComponente){
		Set<Integer> idIncompativeis = cDAO.getIncompatíveis(idComponente);

		return getRemocoes(idIncompativeis);
	}

	public Pair<Set<Integer>,Set<Integer>> getEfeitosSecundariosAdicionarPacote(int idPacote){
		Pacote pacote = pDAO.get(idPacote);
		HashSet<Integer> idIncompativeis = new HashSet<>();
		Set<Integer> componentes = pacote.getComponentes();

		for(int id : componentes)
			idIncompativeis.addAll(cDAO.getIncompatíveis(id));

		return getRemocoes(idIncompativeis);
	}

	private Pair<Set<Integer>,Set<Integer>> getRemocoes(Set<Integer> idIncompativeis){
		HashSet<Integer> incompARemover = new HashSet<>();
		HashSet<Integer> pacotesARemover = new HashSet<>();
		for(int id : idIncompativeis){
			for(Componente c : componentes){
				int idC = c.getId();
				if(idC == id) incompARemover.add(id);
			}
		}

		for(int id : idIncompativeis){
			for(Pacote p : pacotes){
				if(p.getComponentes().contains(id)) pacotesARemover.add(p.getId());
			}
		}

		return new Pair<>(incompARemover, pacotesARemover);
	}

	public void configuracaoOtima() {
		throw new UnsupportedOperationException();
	}
	public void otimizarPacotes() {
		throw new UnsupportedOperationException();
	}

	public Set<Componente> getComponentes() {
		return componentes;
	}

	public Set<Integer> getDependentes() {
		return dependentes;
	}

	public Set<Pacote> getPacotes() {
		return pacotes;
	}

	public ComponenteDAO getcDAO() {
		return cDAO;
	}

	public PacoteDAO getpDAO() {
		return pDAO;
	}

	public void setComponentes(Set<Componente> componentes) {
		this.componentes = componentes;
	}

	public void setDependentes(Set<Integer> dependentes) {
		this.dependentes = dependentes;
	}

	public void setPacotes(Set<Pacote> pacotes) {
		this.pacotes = pacotes;
	}

	public void setcDAO(ComponenteDAO cDAO) {
		this.cDAO = cDAO;
	}

	public void setpDAO(PacoteDAO pDAO) {
		this.pDAO = pDAO;
	}
}
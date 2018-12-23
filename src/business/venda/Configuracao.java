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
		tratarDependencias(componentesAdd);

		//Adição do componente
		this.componentes.add(componente);
		float valorAcrescentado = componente.getPreco();

		//Se foi adicionado um dos dependentes
		boolean escolha = dependentes.contains(idComponente);
			if (escolha) dependentes.remove(idComponente);

		//Formação de pacote
		float descontoFormado = formacaoPacote(idComponente);

		return (valorAcrescentado - valorRetirado - descontoFormado);
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
 	*/
	private void tratarDependencias(Set<Componente> componentes){
		HashSet<Integer> idDependentes = new HashSet<>(); //Retem os id's dos componentes dependentes do Set fornecido
		HashSet aux = new HashSet();					  //Id's dos componentes dependentes de cada um dos componentes do Set fornecido

		//Vai buscar todas as dependências
		for(Componente c : componentes){
			int id = c.getId();
			aux = (HashSet) c.getDepedendencias();
			idDependentes.addAll(aux);
		}
		//Se houver adiciona à lista de dependências da configuração
		if (idDependentes.size() != 0) {
			this.dependentes.addAll(aux);
		}
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

		return valRetirar;
	}
	/*
	 *Remove um componente e as dependencias que gerou
	 *@param idComponente Id componente a ser retirado
	 */
	public float removerComponente(int idComponente) {
		HashSet<Integer> idComponentes = new HashSet<>();
		idComponentes.add(idComponente);

		return removerComponentes(idComponentes);
	}

	/*
	 *Adiciona um pacote e os seus componentes. Trata dependencias, conflitos de pacotes e incompatibilidades.
	 *@param idPacote Id do pacote a ser adicionado
	 *@returns par em que a chave é a quantia a ser descontada na encomenda e o valor é a lista de pacotes que podem ter sido removidos
	 */
	public Pair<Float,List<Integer>> adicionarPacote(int idPacote) throws PacoteJaExisteNaConfiguracao{
		Pacote p = pDAO.get(idPacote);

		if(pacotes.contains(p)) {
			throw new PacoteJaExisteNaConfiguracao("Já existe");
		}

		//tratamentos
		HashSet<Componente> componentes = (HashSet<Componente>)pDAO.getComponentesPacote(idPacote);
		float valorRetirar = tratarIncompatibilidades(componentes);
		tratarDependencias(componentes);

		return adicionaPacoteTrataConflitos(p, componentes, valorRetirar);


	}
	/*
	 *Resolve os conflitos e adiciona o pacote e os seu componentes.
	 *Resolver os conflitos é remover os pacotes cujos componentes também pertencem ao adicionado
	 *@pacote Pacote a adicionar
	 *@param componentes Componentes desse pacote
	 *@param valorRetirar Valor que será retornado com outros cálculos intermédios
	 */
	private Pair<Float,List<Integer>> adicionaPacoteTrataConflitos(Pacote pacote, Set<Componente> componentes, float valorRetirar){
		ArrayList<Integer> pacotesRemovidos = new ArrayList<>();  //Pacotes que serão removidos
		Set<Integer> compIds;									  //Componentes de cada pacote
		float val = valorRetirar;

		//Por cada pacote da config. verificamos se contém componentes do pacote que está a ser adicionado
		for(Pacote p : pacotes){
			compIds = p.getComponentes();
			val+=removeConflitos(p, componentes, compIds, pacotesRemovidos);
		}
		//Adicção do pacote e desconto formado pelo pacote
		val-=pacote.getDesconto();
		pacotes.add(pacote);


		return new Pair<>(val, pacotesRemovidos);
	}

	private float removeConflitos (Pacote p, Set<Componente> componentes, Set<Integer> compIds, ArrayList<Integer> pacotesRemovidos){
		boolean found = false;
		Iterator<Componente> it = componentes.iterator();
		float descontoRetirar = 0;
		float precoComponente = 0;

		while(it.hasNext() && !found){
			int idC = it.next().getId();
			found = compIds.contains(idC);
			precoComponente += it.next().getPreco();
			if(found){
				pacotes.remove(p);
				int idP = p.getId();
				pacotesRemovidos.add(idP);
				descontoRetirar += p.getDesconto();
			}
		}
		return descontoRetirar + precoComponente;
	}

	public void removerPacote() {
		throw new UnsupportedOperationException();
	}

	public void configuracaoOtima() {
		throw new UnsupportedOperationException();
	}

	public void otimizarPacotes() {
		throw new UnsupportedOperationException();
	}
}
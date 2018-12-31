package business.venda;

import business.produtos.ComparaPacotesByDesconto;
import business.produtos.Componente;
import business.produtos.Pacote;
import business.produtos.PacoteDormente;
import business.venda.categorias.Categoria;
import data.ComponenteDAO;
import data.PacoteDAO;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.*;

public class Configuracao {
	private HashMap<Integer,Componente> componentes; //Componentes da configuração
	private Set<Integer> dependentes; 	 //Id's dos componentes que precisam de ser adicionados
	private HashMap<Integer, Pacote> pacotes;		 //Pacotes da configuração
	private HashMap<Integer, PacoteDormente> pacotesDormentes;  //Pacotes que deixaram de estar ativos, mas podem voltar.
	private ComponenteDAO cDAO;
	private PacoteDAO pDAO;

	public Configuracao() {
		this.componentes = new HashMap<>();
		this.dependentes = new HashSet<>();
		this.pacotes = new HashMap<>();
		this.pacotesDormentes = new HashMap<>();
		this.cDAO = new ComponenteDAO();
		this.pDAO = new PacoteDAO();
	}

	/*
	 * Adiciona um componente, tratando as incompatibilidades e dependencias, assim como a formação de um pacote.
	 * @param idComponente id do componente a adicionar
	 * @returns valor a acrescentar à encomenda
	 */
	public Set<Integer> adicionarComponente(int idComponente) throws SQLException {
		Componente componente = cDAO.get(idComponente);
		//Manhas para não fazer métodos diferentes. Todos recebem lista
		HashSet<Componente> componentesAdd = new HashSet<>();
		componentesAdd.add(componente);

		//Tratamentos
		//Valor retirado corresponde ao valor que os componentes a remover tinham e que vai ser subtraído
		//Valor Acrescentado é o valor dos componentes novos a adicionar. no tratarDependencias somente pq
		//faço lá o ciclo necessário para ter os valores.
		Set<Integer> pac = tratarIncompatibilidades(componentesAdd);
		tratarDependencias(componentesAdd);

		//Adição do componente
		this.componentes.put(componente.getId(),componente);

		//Se foi adicionado um dos dependentes !! Deprecated, pq se eu retirar um dos dependentes e depois o vendedor
		//remove-lo eu não sei que ele é dependente.
		//boolean escolha = dependentes.contains(idComponente);
		//if (escolha) dependentes.remove(idComponente);

		//Formação de pacote
		int descontoAcrescentado;
		//Damos prioridade aos dormentes. Podiam haver mais pacotes possíveis a ser formados, mas como não aceitamos conflitos
		//estes componentes estão reservados a pacotes inativos
		//Pacotes inativos podem ser de componentes removidos normalmente e não de dependentes, mas também parece-me indicado
		//dar prioridade a estes.

		if (pacotesDormentes.containsKey(idComponente)){
			PacoteDormente pd = pacotesDormentes.get(idComponente);
			boolean flag = pd.decr();
			pacotes.put(pd.getId(), new Pacote(pd));
		}
		else {
			formacaoPacote(componente);
		}

		return pac;
	}
	/*
	 *Verifica se houve formou de algum pacote com a adição de um componente
	 *@param idComponente id do componente que poderá ter formado pacote
	 *@returns val valor a descontar para o total da encomenda
	 */
	public void formacaoPacote(Componente componente) throws SQLException {
		List<Pacote> pac;    //Pacotes que tem na sua constituição o componente fornecido como parámetro
		HashSet<Integer> aux;    //Componentes de um pacote
		TreeSet<Pacote> formados = new TreeSet<>(new ComparaPacotesByDesconto()); // pacotes que podem ser formados
		pac = pDAO.list(componente);
		int count;            	//Irá indicar se pode ser formado o pacote

		//!!!Talvez possa otimizar e trazer os pacotes para a memória, mas não sei se vale a pena
		//Por cada pacote que na sua constituição tem o componente vamos verificar se temos todos os componentes necessários
		for (Pacote p : pac) {
			aux = (HashSet<Integer>) p.getComponentes();
			count = 0;
			for (int id : aux) {
				for (int key : componentes.keySet())
					if (id == key)
						count++;
			}
			if (count == aux.size()) formados.add(p);
		}

		//É retirado o pacote com menor custo da estrutura auxiliar e adicionado à configuração

		if(formados.size()!=0){
			Pacote p = formados.first();
			pacotes.put(p.getId(), p);
			}

	}
	/*
	 *Vai buscar as incompatibilidades dos componentes que recebeu como argumento e remove-as
	 *@param componentes Componentes cujas incompatibilidades serão removidas
	 */
	private Set<Integer> tratarIncompatibilidades(Set<Componente> comp) throws SQLException {
		HashSet<Integer> idDepInc = new HashSet<>();  //Id's dos componentes incompatíveis de todos os componentes
		Set<Integer> aux;
		Set<Integer> rem;
		HashSet<Integer> idIncompativeis = new HashSet<>();

		//Vai buscar todas as incompatibilidades dos componentes
		for(Componente c : comp){
			aux = c.getDependentesDasIncompatibilidades();
			idDepInc.addAll(aux);
			rem = c.getIncompatibilidades();
			idIncompativeis.addAll(rem);
		}
		for(Componente c : comp){
			for(Componente cf : componentes.values()){
				if(cf.getCategoria().equals(c.getCategoria()))
					idIncompativeis.add(cf.getId());
			}
		}

		return removerComponentes(idIncompativeis, idDepInc);
	}
	/*
	 *Vai buscar as dependências dos componentes a remover e remóveas da lista dependentes.
	 *Remove os componentes recebidos. Desfaz os pacotes onde estes componentes estavam,
	 *@param idComponentes Componentes a ser removidos
	 *@returns valor a ser diminuido ao valor da encomenda
	 */
	private Set<Integer> removerComponentes (Set<Integer> componentesARemover, Set<Integer> idDependentesInc) throws SQLException {
		Set<Integer> pac;
		Iterator<Integer> it;

		pac = desfazPacotes(componentesARemover);

		//Retira os componentes da config. e também o seu valor
		for(int id : componentesARemover){
			if(componentes.containsKey(id)){
				Componente c = componentes.get(id);
				componentes.remove(id,c);}
			}
		removeDependentes(idDependentesInc);
		return pac;
	}
	private void removeDependentes (Set<Integer> idDependentesInc) throws SQLException {
		boolean found; //para não tirar o desconto várias vezes

		//Retira os dependentes da config.
		for(int idDremov : idDependentesInc) {
			if(dependentes.contains(idDremov)){
				found =false;
				for (Componente c : componentes.values()) {
					Set<Integer> dep = c.getDepedendencias();
					if(dep.contains(idDremov))
						found=true;
				}
				if(!found) dependentes.remove(idDremov);
			}
		}
	}
	private Set<Integer> desfazPacotes (Set<Integer> componentesARemover){
		HashSet<Integer> pac = new HashSet<>();
		Iterator<Integer> it;
		boolean found;
		for(Pacote p : pacotes.values()) {
			found = false;
			it = p.getComponentes().iterator();
			while (it.hasNext() && !found){
				int id = it.next();
				for (int idCR : componentesARemover) {
					if (id  == idCR && componentes.containsKey(idCR)) {
						if(!found){
							int idP = p.getId();
							pacotes.remove(idP,p);
							pac.add(idP);
							found = true;}
					}
				}
			}
		}
		return pac;
	}

	/*
	 *Vais buscar dependencias e adiciona-as a this.dependentes
	 *@param componentes Componentes cujas dependências terão de ser adicionadas
	 *@returns val Valor dos componentes que vão ser adicionados a this.componentes ---- para aproveitar o ciclo
	 */
	private void tratarDependencias(Set<Componente> componentes) throws SQLException {
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
			this.dependentes.addAll(idDependentes);
		}
	}
	/*
	 *Remove um componente e as dependencias que gerou
	 *@param idComponente Id componente a ser retirado
	 */
	public Set<Integer> removerComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao, SQLException {
		if (!componentes.containsKey(idComponente)) throw new ComponenteNaoExisteNaConfiguracao("");

		HashSet<Integer> comp = new HashSet<>();
		Componente c = componentes.get(idComponente);
		Set<Integer> aux = c.getDepedendencias();
		comp.add(c.getId());

		return removerComponentes(comp, aux);
	}

	/*
	 *Adiciona um pacote e os seus componentes. Trata dependencias, conflitos de pacotes e incompatibilidades.
	 *@param idPacote Id do pacote a ser adicionado
	 *@returns par em que a chave é a quantia a ser descontada na encomenda e o valor é a lista de pacotes que podem ter sido removidos
	 */
	public Set<Integer> adicionarPacote(int idPacote) throws SQLException {
		Pacote p = pDAO.get(idPacote);
		HashSet<Componente> componentes = (HashSet<Componente>) p.getComponentesRef();

		//tratamentos
		Set<Integer> pac = tratarIncompatibilidades(componentes);
		int valorAcrescentado = tratarDependenciasPacote(componentes,p);

		for(int id : p.getComponentes()){
			Componente c = cDAO.get(id);
			this.componentes.put(id,c);
		}
		return pac;
	}
	private int tratarDependenciasPacote(Set<Componente> componentes, Pacote p) throws SQLException {
		HashSet<Integer> idDependentes = new HashSet<>(); //Retem os id's dos componentes dependentes do Set fornecido
		HashSet aux = new HashSet();					  //Id's dos componentes dependentes de cada um dos componentes do Set fornecido
		int val = 0;

		//Vai buscar todas as dependências
		for(Componente c : componentes){
			int id = c.getId();
			aux = (HashSet) c.getDepedendencias();
			idDependentes.addAll(aux);
		}
		//Se houver adiciona à lista de dependências da configuração
		if (idDependentes.size() == 0) {
			this.pacotes.put(p.getId(),p);
		}
		this.dependentes.addAll(idDependentes);

		PacoteDormente pd = new PacoteDormente(p,0);
		for(int id : idDependentes) {
			pd.incr();
			pacotesDormentes.put(id, pd);
		}
		return val;
	}

	private boolean existeConflito(Set<Integer> componentes) {
		boolean found = false;
		Iterator<Pacote> it = pacotes.values().iterator();
		Set<Integer> compIds;

		while (it.hasNext() && !found) {
			compIds = it.next().getComponentes();
			for (int idC : componentes) {
				found = compIds.contains(idC);
			}
		}
		return found;
	}
	public Set<Integer> removerPacote(int idPacote) throws PacoteNaoExisteNaConfiguracaoException, SQLException {
		if(!pacotes.containsKey(idPacote)) throw new PacoteNaoExisteNaConfiguracaoException("Pacote não existe");

		Pacote p = pacotes.get(idPacote);
		Set<Componente> componentesPacote = p.getComponentesRef();
		Set<Integer> aux = new HashSet<>();
		Set<Integer> componentesARemover = new HashSet<>();

			for(Componente c : componentesPacote) {
				aux.addAll(c.getDependentesDasIncompatibilidades());
				componentesARemover.add(c.getId());
			}

		return removerComponentes(componentesARemover, aux);

	}
	//meter a dar throw de exceção que não existem incompatibilidades
	public Set<Integer> getIncompatibilidadesComponente (int idComponente) throws ComponenteJaExisteNaConfiguracaoException, SQLException {
		Componente c = cDAO.get(idComponente);
		if(componentes.containsKey(idComponente)) throw new ComponenteJaExisteNaConfiguracaoException("Já existe");

		Set<Integer> idIncompativeis = c.getIncompatibilidades();

		Set<Integer> remocoes = getRemocoes(idIncompativeis);
		return remocoes;
	}

	public Set<Integer> getIncompatibilidadesPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException, SQLException {
		Pacote pacote = pDAO.get(idPacote);

		if(pacotes.containsKey(idPacote) || pacotesDormentes.containsValue(pacote)) throw new PacoteJaExisteNaConfiguracaoException("Já existe");

		if(existeConflito(pacote.getComponentes())) {
			throw new PacoteGeraConflitosException("Já existe um pacote com algum dos componentes do que pretende adicionar");
		}

		HashSet<Integer> idIncompativeis = new HashSet<>();
		Set<Componente> componentes = pacote.getComponentesRef();

		for(Componente c : componentes){
			idIncompativeis.addAll(c.getIncompatibilidades());
		}

		Set<Integer> remocoes = getRemocoes(idIncompativeis);
		return remocoes;
	}
	public int getValorConfiguracao(){
		int val=0;
		for(Componente c : componentes.values()){
			val+=c.getPreco();
		}
		for(Pacote p : pacotes.values()){
			val-=p.getDesconto();
		}
		return val;
	}
	private Set<Integer> getRemocoes(Set<Integer> idIncompativeis){
		HashSet<Integer> incompARemover = new HashSet<>();
		//HashSet<Integer> pacotesARemover = new HashSet<>();
		for(int id : idIncompativeis){
			for(int idC : componentes.keySet()){
				if(idC == id) incompARemover.add(id);
			}
			//for(Pacote p : pacotes){
			//	if(p.getComponentes().contains(id)) pacotesARemover.add(p.getId());
			//}
		}
		return incompARemover;
		//return new Pair<>(incompARemover, pacotesARemover);
	}

	public List<Integer> otimizarPacotes() throws SQLException {
		Set<Pacote> todosPacotes = new HashSet<>();
		for (Componente c : componentes.values()){
			todosPacotes.addAll(pDAO.list(c));
		}
		Set<Pacote> otimos = calculaOtimos(todosPacotes);
		boolean reotimizacao = comparaPacotes(otimos);
		if(reotimizacao) {
			pacotes.clear();
			for (Pacote p : otimos) {
				pacotes.put(p.getId(), p);
			}
		}
		return new ArrayList<>(pacotes.keySet());
	}

	private Set<Pacote> calculaOtimos(Set<Pacote> pacotes){
		HashSet<Pacote> solucao = new HashSet<>();
		HashSet<Integer> componentes = new HashSet<>();
		ArrayList<Pacote> porAdicionar = new ArrayList<>(pacotes);
		porAdicionar.sort(new Comparator<Pacote>() {
			@Override
			public int compare(Pacote p1, Pacote p2) {
				return -Integer.compare(p2.getDesconto(), p1.getDesconto());
			}
		});

		for(Pacote pacote:porAdicionar) {
			Set<Integer> componentesPacote = pacote.getComponentes();
			if (Collections.disjoint(componentesPacote, componentes)) {
				solucao.add(pacote);
				componentes.addAll(componentesPacote);
			}
		}

		return solucao;
	}

	private boolean comparaPacotes(Set<Pacote> otimos){
		int valAtual = 0;
		int valOtimo = 0;

		//Por ser heurística
		for(Pacote p : otimos) valOtimo+=p.getDesconto();
		for(Pacote p : pacotes.values()) valAtual+=p.getDesconto();

		if(valOtimo > valAtual) return true;

		return false;
	}

	public Set<Componente> atualizaStock() throws SQLException, FaltamDependentesException {
		for(int id : dependentes){
			if (!componentes.keySet().contains(id))
				throw new FaltamDependentesException("Insira a seguinte dependência: " + Integer.toString(id));
		}

		return cDAO.atualizaStock(new ArrayList<Componente>(componentes.values()));
	}
	public int getDesconto(){
		int val = 0;
		for(Pacote p : pacotes.values()){
			val+= p.getDesconto();
		}
		return val;
	}

	public boolean configuracaoOtima(Map<Categoria, Integer> precoMaximoCategorias, int precoMaximoTotal) throws SQLException {
		Map<Categoria, List<Componente>> componentesCategorias = new HashMap<>();
		Map<Categoria, Componente> solucaoCategoria = new HashMap<>();

		for(Map.Entry<Categoria, Integer> entry : precoMaximoCategorias.entrySet()) {
			Categoria categoria = entry.getKey();
			int precoMax = entry.getValue();
			List<Componente> componentesCategoria = new ComponenteDAO().list(categoria); //Lista componentes da categoria
			//Filtra os componentes com preço dentro do limite
			List<Componente> componentes = new ArrayList<>(componentesCategoria.size());
			for (Componente c:componentesCategoria){
				if(c.getPreco() <= precoMax){
					componentes.add(c);
				}
			}
			//Ordena-os por ordem crescente
			componentes.sort(new Comparator<Componente>() {
				@Override
				public int compare(Componente c1, Componente c2) {
					return Integer.compare(c1.getPreco(), c2.getPreco());
				}
			});
			//Cada categoria tem a sua lista de componentes
			componentesCategorias.put(categoria, componentes);
			//Coloca o componente com maior preço
			solucaoCategoria.put(categoria, componentes.get(componentes.size() - 1));
		}

		do {
			Set<Componente> solucaoTodosComponentes = new HashSet<>(getComponentes());
			Collection<Componente> solucaoComponente = solucaoCategoria.values();
			solucaoTodosComponentes.addAll(solucaoCategoria.values());
			Set<Pacote> solucaoPacote = calculaOtimos(getPacotesValidos(solucaoTodosComponentes));
			// Calcula o preço total da configuração com os novos componentes
			int precoTotal = 0;
			for (Componente componente : solucaoComponente) {
				precoTotal += componente.getPreco();
			}
			for (Pacote pacote : solucaoPacote) {
				precoTotal -= pacote.getDesconto();
			}
			if(precoTotal <= precoMaximoTotal) {
				componentes.clear();
				for(Componente componente : solucaoTodosComponentes){
					componentes.put(componente.getId(), componente);
				}
				if(comparaPacotes(solucaoPacote)){
					//Adiciona o pacote se este for melhor
					pacotes.clear();
					for (Pacote pacote : solucaoPacote){
						pacotes.put(pacote.getId(), pacote);
					}
				}
				return true; // Encontrou-se a solução ótima
			} else {
				int minDiferencaDePreco = 0;
				Pair<Categoria, Componente> proximoEntrar = null; //Próximo a entrar na solução
				//Procura o componente com menor diferença de preço em relação ao anterior
				for(Map.Entry<Categoria, List<Componente>> entry : componentesCategorias.entrySet()){
					Categoria categoria = entry.getKey();
					List<Componente> list = entry.getValue();
					int size = list.size();
					if(size >= 1){ // Tem proximo na categoria
						Componente atual = list.get(size - 1);
						Componente proximo = list.get(size - 2);
						int diferencaDePreco = atual.getPreco() - proximo.getPreco();
						if(diferencaDePreco <= minDiferencaDePreco) {
							minDiferencaDePreco = diferencaDePreco;
							proximoEntrar = new Pair<>(categoria, proximo);
						}
					}
				}
				if(proximoEntrar == null) {
					return false; // Não encontrou solução ótima
				} else {
					List<Componente> list = componentesCategorias.get(proximoEntrar.getKey());
					list.remove(list.size() - 1); // Remove o último elemento da lista
					solucaoCategoria.put(proximoEntrar.getKey(), proximoEntrar.getValue());
				}
			}
		} while (true);
	}

	private Set<Pacote> getPacotesValidos(Collection<Componente> componentes) throws SQLException {
		Map<Pacote, Integer> numeroComponentesPacote = new HashMap<>();
		Set<Pacote> pacotesValidos = new HashSet<>();
		for (Componente componente : componentes){
			List<Pacote> pacotesComponente = new PacoteDAO().list(componente);
			for (Pacote pacote : pacotesComponente) {
				int total = numeroComponentesPacote.getOrDefault(pacote, -1);
				total++;
				if(total == pacote.getComponentes().size()){
					pacotesValidos.add(pacote);
				} else {
					numeroComponentesPacote.put(pacote, total);
				}
			}
		}
		return pacotesValidos;
	}

	public List<Componente> getComponentes() {
		return new ArrayList<Componente>(componentes.values());
	}

	public Set<Integer> getDependentes() {
		return new HashSet<>(dependentes);
	}

	public List<Pacote> getPacotes() {
		return new ArrayList<>(pacotes.values());
	}

	public List<Componente> getComponentesOpcionais (){
		ArrayList<Componente> res = new ArrayList<>();
			for(Componente c : componentes.values()){
				if(!c.getCategoria().getObrigatoria()){
					res.add(c);
				}
			}
		return res;
	}
	public List<Componente> getComponentesObrigatórios (){
		ArrayList<Componente> res = new ArrayList<>();
		for(Componente c : componentes.values()){
			if(c.getCategoria().getObrigatoria()){
				res.add(c);
			}
		}
		return res;
	}

	public void setcDAO(ComponenteDAO cDAO) {
		this.cDAO = cDAO;
	}

	public void setpDAO(PacoteDAO pDAO) {
		this.pDAO = pDAO;
	}
}
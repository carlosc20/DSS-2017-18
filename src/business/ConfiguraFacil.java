package business;// import Venda.Encomenda;
// import Diagrama_de_packages.Business.Encomenda;

import java.io.File;
import java.util.List;

public class ConfiguraFacil {

	private static ConfiguraFacil instancia = new ConfiguraFacil();
	/*
	private Utilizador utilizadorAtual;
	private Venda.Encomenda encomendaAtual;
	private CategoriaDAO categorias;
	private EncomendaEmProducaoDAO filaProducao;
	private ComponenteDAO todosComponentes;
	private PacoteDAO todosPacotes;
	private EncomendaDAO encomendas; // nome corrigido
	private UtilizadorDAO utilizadores;
	*/

	public static ConfiguraFacil getInstancia() {
		return instancia;
	}

	private ConfiguraFacil(){};

	public int autenticar(String nome, String password) throws Exception {
		if (nome.equals("administrador")) return 1;
		if (nome.equals("vendedor")) return 2;
		if (nome.equals("repositor")) return 3;
		return 0;
	}

	public void criarEncomenda(String cliente, int nif) { //muda nome
		throw new UnsupportedOperationException();
	}

	public List<Integer> veIncompatibilidades(int aId) {
		throw new UnsupportedOperationException();
	}

	public List<Integer> adicionaComponente(int aIdComponente) {
		throw new UnsupportedOperationException();
	}

	public void removeComponente(int aId) {
		throw new UnsupportedOperationException();
	}

	public List<Integer> adicionaPacote(int aId) {
		throw new UnsupportedOperationException();
	}

	public void removePacote(int aId) {
		throw new UnsupportedOperationException();
	}

	public void configuracaoOtima() {
		throw new UnsupportedOperationException();
	}

	public float finalizaEncomenda() {
		throw new UnsupportedOperationException();
	}

	public void atualizarStock(File file) { // mudou nome, mudou tipo argumento
		throw new UnsupportedOperationException();
	}

	public void criarUtilizador(String nome, String aPassword, int aTipo) {
		throw new UnsupportedOperationException();
	}

	public void removerUtilizador(String nome) {
		throw new UnsupportedOperationException();
	}

	public void consultarStock() {
		throw new UnsupportedOperationException();
	}

	public void consultarConfiguracao() {
		throw new UnsupportedOperationException();
	}

	public void consultarRegistoProduzidas() {
		throw new UnsupportedOperationException();
	}

	public void consultarFilaProducao() {
		throw new UnsupportedOperationException();
	}

	private boolean otimizarPacotes() {
		throw new UnsupportedOperationException();
	}

	private void formacaoPacote(int aId) {
		throw new UnsupportedOperationException();
	}

	public List<String> getFuncionarios(){return null;}

	// TODO: consultar pacotes e componentes para o repositor; funcionarios para o admin; criar/remover utilizador
	/*
	private void colocaNaFila(Diagrama_de_packages.Business.Encomenda aEncomendaAtual, List<Integer> aEmFalta) {
		throw new UnsupportedOperationException();
	}
	*/
}
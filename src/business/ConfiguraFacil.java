package business;

import business.utilizadores.Utilizador;
import business.venda.Encomenda;
import data.*;

import java.util.List;

public class ConfiguraFacil {
	private Utilizador _utilizadorAtual;
	private Encomenda _encomendaAtual;
	public CategoriaDAO _categorias;
	public EncomendaEmProducaoDAO filaProducao;
	public ComponenteDAO _todosComponentes;
	public PacoteDAO _todosPacotes;
	public EncomendaDAO _encomendas;
	public UtilizadorDAO utilizadores_;

	public int autenticar(String aNome, String aPassword) {
		throw new UnsupportedOperationException();
	}

	public void criaEncomenda(String aCliente, int aNif) {
		throw new UnsupportedOperationException();
	}

	public List<Integer> veIncompatibilidades(int aId) {
		throw new UnsupportedOperationException();
	}

	public void resolveIncompatibilidades(int aId) {
		throw new UnsupportedOperationException();
	}

	public void adicionaComponente(int aId) {
		throw new UnsupportedOperationException();
	}

	public void removeComponente(int aId) {
		throw new UnsupportedOperationException();
	}

	public void adicionaPacote(int aId) {
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

	public void atualizaStock(String aPath) {
		throw new UnsupportedOperationException();
	}

	public void criaUtilizador(String aNome, String aPassword, int aTipo) {
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

	private void otimizarPacotes() {
		throw new UnsupportedOperationException();
	}
}
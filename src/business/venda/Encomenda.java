package business.venda;


import data.ComponenteDAO;
import data.PacoteDAO;

public class Encomenda {
	private int id;
	private String cliente;
	private int nif;
	private float valor;

	public Encomenda() {}

	public Encomenda(int _id, String _cliente, int _nif, ComponenteDAO cDAO, PacoteDAO pDAO) {
		this.id = _id;
		this.cliente = _cliente;
		this.nif = _nif;
		this.valor = 0;
		this.configuracao = new Configuracao(cDAO, pDAO);
	}

	public Configuracao configuracao;

	public void veIncompatibilidades() {
		throw new UnsupportedOperationException();
	}

	public void resolveIncompatibilidades(int aId) {
		throw new UnsupportedOperationException();
	}

	public void adicionaComponente() {
		throw new UnsupportedOperationException();
	}

	public void removeComponente() {
		throw new UnsupportedOperationException();
	}

	public void adicionaPacote() {
		throw new UnsupportedOperationException();
	}

	public void removePacote() {
		throw new UnsupportedOperationException();
	}

	public void configuracaoOtima() {
		throw new UnsupportedOperationException();
	}

	public void otimizaPacotes() {
		throw new UnsupportedOperationException();
	}
}
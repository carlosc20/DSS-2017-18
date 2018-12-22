package business.venda;

public class Encomenda {
	private int id;
	private String cliente;
	private int nif;
	private float valor;

	public Encomenda(int _id, String _cliente, int _nif) {
		this.id = _id;
		this.cliente = _cliente;
		this.nif = _nif;
		this.valor = 0;
		this.configuracao = new Configuracao();
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
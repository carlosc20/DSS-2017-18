package business.venda;

import business.venda.Configuracao;

public class Encomenda {
	private int _id;
	private String _cliente;
	private int _nif;
	private float _valor = 0;
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
package business.venda;

import business.produtos.Pacote;

import java.util.ArrayList;
import java.util.List;

public class Configuracao {
	private List<Integer> componentes;
	private List<Integer> dependentes;
	public  List<Pacote> pacotes;

	public Configuracao() {
		this.componentes = new ArrayList<Integer>();
		this.dependentes = new ArrayList<Integer>();
		this.pacotes = new ArrayList<Pacote>();
	}

	public void adicionarComponente(int idComponente) {

	}
	private void tratarIncompatibilidades(int idComponente)
	public void removerComponente() {
		throw new UnsupportedOperationException();
	}

	public void adicionarPacote() {
		throw new UnsupportedOperationException();
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
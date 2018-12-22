package business.produtos;

import business.venda.Configuracao;

import java.util.List;
import java.util.Set;

public class Pacote {
	private int _id;
	private String _designacao;
	private int _desconto;
	private List<Integer> _componentes;
	private Configuracao _pacotes;

	public Set<Componente> getIncompativeis() {
		throw new UnsupportedOperationException();
	}

	public Set<Componente> getDependencias() {
		throw new UnsupportedOperationException();
	}
}
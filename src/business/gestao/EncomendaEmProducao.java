package business.gestao;

import business.produtos.Componente;
import business.venda.Encomenda;

import java.util.HashSet;
import java.util.Set;

public class EncomendaEmProducao extends Encomenda {
	public Set<Componente> componentesEmFalta = new HashSet<Componente>();

	public void fornecerComponente(int aId) {
		throw new UnsupportedOperationException();
	}

	public Set<Componente> getComponentesEmFalta() {
		return componentesEmFalta;
	}
}
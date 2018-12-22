package business.gestao;

import java.util.Vector;
import business.produtos.Componente;
import business.venda.Encomenda;

public class EncomendaEmProducao extends Encomenda {
	public Vector<Componente> _componentesEmFalta = new Vector<Componente>();

	public void fornecerComponente(int aId) {
		throw new UnsupportedOperationException();
	}
}
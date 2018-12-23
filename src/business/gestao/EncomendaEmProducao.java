package business.gestao;

import java.util.Vector;
import business.produtos.Componente;
import business.venda.Encomenda;

public class EncomendaEmProducao extends Encomenda {
	public Vector<Componente> componentesEmFalta = new Vector<Componente>();

	public EncomendaEmProducao(int _id, String _cliente, int _nif) {
		super(_id, _cliente, _nif);
	}

	public void fornecerComponente(int aId) {
		throw new UnsupportedOperationException();
	}
}
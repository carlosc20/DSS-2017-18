package data;

import business.produtos.Componente;

import java.util.List;
import java.util.Set;

public class ComponenteDAO extends DAO {

	public void put(String aId, Componente aComponente) {
		throw new UnsupportedOperationException();
	}

	public Set<Componente> list(String aCondition) {
		throw new UnsupportedOperationException();
	}

	public Componente get(int aId) {
		throw new UnsupportedOperationException();
	}

	public void remove(Componente aComponente) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}
	public Set<Integer> getIncompatíveis(int idComponente){throw new UnsupportedOperationException();}

	public Set<Integer> getDependentes(int idComponente){throw new UnsupportedOperationException();}
}
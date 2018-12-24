package data;

import business.produtos.Componente;

import java.util.HashSet;
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

	public Set<Integer> atualizaStock(Set<Componente> componentes){
		// Retorna os componentes com stock 0 que pertencem ao parametro fornecido
		Set<Integer> emFalta = getIdStockZero(componentes);
		// decrementa o stock de cada um em 1. Cuidado para não ficar neg.
		decrementaStock(componentes);
		return emFalta;

	}
	public void decrementaStock(Set<Componente> componentes){throw new UnsupportedOperationException();}
	public Set<Integer> getIdStockZero(Set<Componente> componentes){throw new UnsupportedOperationException();}
}
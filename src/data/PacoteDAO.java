package data;

import business.produtos.Componente;
import business.produtos.Pacote;

import java.util.List;
import java.util.Set;

public class PacoteDAO extends DAO {

	public void put(String aId, Pacote aPacote) {
		throw new UnsupportedOperationException();
	}

	public List<Pacote> list(String aCondition) {
		throw new UnsupportedOperationException();
	}

	public Pacote get(int aId) {
		throw new UnsupportedOperationException();
	}

	public void remove(Pacote aPacote) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		throw new UnsupportedOperationException();
	}

	public Set<Pacote> getPacotesComComponente(int idComponente) {
		throw new UnsupportedOperationException();
	}
	public Set<Componente> getComponentesPacote(int idPacote){throw new UnsupportedOperationException();}
	public Set<Pacote> getPacotesCorrespondentes(Set<Componente> componentes) {throw new UnsupportedOperationException();}
}
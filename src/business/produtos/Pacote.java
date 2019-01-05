package business.produtos;

import data.ComponenteDAO;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Pacote {
	private int id;
	private String designacao;
	private int desconto;
	private Set<Integer> componentes;
	private ComponenteDAO cDAO;

	public Pacote(int id, String designacao, int desconto, Set<Integer> componentes) {
		this.id = id;
		this.designacao = designacao;
		this.desconto = desconto;
		this.componentes = componentes;
		this.cDAO = new ComponenteDAO();
	}
	public Set<Componente> getComponentesRef() throws SQLException {
		HashSet<Componente> res = new HashSet<>();

		for(int id : componentes){
			res.add(cDAO.get(id));
		}
		return res;
	}

	public Pacote(Pacote p) {
		this.id = p.getId();
		this.designacao = p.getDesignacao();
		this.desconto = p.getDesconto();
		this.componentes = p.getComponentes();
		this.cDAO = new ComponenteDAO();
	}
	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;

		if ((o==null) || (this.getClass() != o.getClass()))
			return false;

		Pacote p = (Pacote) o;
		return (this.id == p.getId());
	}


	public ComponenteDAO getcDAO() {
		return cDAO;
	}

	public Set<Integer> getComponentes() {
		return componentes;
	}

	public int getId() {
		return id;
	}

	public String getDesignacao() {
		return designacao;
	}

	public int getDesconto() {
		return desconto;
	}

	public String toString() { return Integer.toString(id); }
}
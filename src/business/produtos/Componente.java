package business.produtos;

import business.venda.categorias.Categoria;
import data.ComponenteDAO;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Componente {
	private int id;
	private String designacao;
	private int preco;
	private int stock;
	private Set<Integer> depedendencias;
	private Set<Integer> incompatibilidades;
	private Categoria categoria;
	private ComponenteDAO componentes;

	public Componente(int id, String designacao, int preco, int stock, Set<Integer> depedendencias, Set<Integer> incompatibilidades, Categoria categoria) {
		this.id = id;
		this.designacao = designacao;
		this.preco = preco;
		this.stock = stock;
		this.depedendencias = depedendencias;
		this.incompatibilidades = incompatibilidades;
		this.categoria = categoria;
		this.componentes = new ComponenteDAO();
	}

	public void decrementaStock() {
		stock--;
	}

	public Set<Integer> getDependentesDasIncompatibilidades() throws SQLException {
		HashSet<Integer> idIncompativeis = new HashSet<>();
		Set<Integer> aux;

		for(int id : incompatibilidades){
			aux = componentes.getDependentes(id);
			idIncompativeis.addAll(aux);
		}
		return idIncompativeis;
	}
	@Override
	public boolean equals(Object o){
		if (this == o)
			return true;

		if ((o==null) || (this.getClass() != o.getClass()))
			return false;

		Componente c = (Componente) o;
		return (this.id == c.getId());
	}
	public int getId() {
		return id;
	}

	public String getDesignacao() {
		return designacao;
	}

	public int getPreco() {
		return preco;
	}

	public int getStock() {
		return stock;
	}

	public Set<Integer> getDepedendencias() throws SQLException {
		if(depedendencias == null){
			depedendencias = componentes.getDependentes(id);
		}
		return depedendencias;
	}

	public Set<Integer> getIncompatibilidades() throws SQLException {
		if(incompatibilidades == null){
			incompatibilidades = componentes.getIncompativeis(id);
		}
		return incompatibilidades;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public String toString(){
		return designacao;
	}
}
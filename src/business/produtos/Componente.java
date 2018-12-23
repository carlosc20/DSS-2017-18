package business.produtos;

import business.venda.categorias.Categoria;

import java.util.Set;

public class Componente {
	private int id;
	private String designacao;
	private int preco;
	private int stock;
	private Set<Integer> depedendencias;
	private Set<Integer> incompatibilidades;
	private Categoria categoria;

	public Componente(int id, String designacao, int preco, int stock, Set<Integer> depedendencias, Set<Integer> incompatibilidades, Categoria categoria) {
		this.id = id;
		this.designacao = designacao;
		this.preco = preco;
		this.stock = stock;
		this.depedendencias = depedendencias;
		this.incompatibilidades = incompatibilidades;
		this.categoria = categoria;
	}

	public void decrementaStock() {
		stock--;
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

	public Set<Integer> getDepedendencias() {
		return depedendencias;
	}

	public Set<Integer> getIncompatibilidades() {
		return incompatibilidades;
	}

	public Categoria getCategoria() {
		return categoria;
	}
}
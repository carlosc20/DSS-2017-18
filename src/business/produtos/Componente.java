package business.produtos;

import business.venda.categorias.Categoria;

import java.util.ArrayList;
import java.util.List;

public class Componente {
	private int _id;
	private String _designacao;
	private int _preco;
	private int _stock;
	public List<Componente> _depedendencias = new ArrayList<Componente>();
	public List<Componente> incompatibilidades = new ArrayList<Componente>();
	private Categoria categoria;

	public void decrementaStock() {
		throw new UnsupportedOperationException();
	}
}
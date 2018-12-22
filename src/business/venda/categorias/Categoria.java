package business.venda.categorias;

import business.produtos.Componente;

import java.util.Vector;

public abstract class Categoria {
	private String _designacao;
	public Vector<Componente> _componentes = new Vector<Componente>();
}
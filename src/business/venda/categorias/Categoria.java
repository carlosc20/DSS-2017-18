package business.venda.categorias;

import business.produtos.Componente;

import java.util.Vector;

public abstract class Categoria {
	private String designacao;
	public Vector<Componente> _componentes = new Vector<Componente>();

	public Categoria(String designacao){
		this.designacao = designacao;
	}

	public void setDesignacao(String designacao){
		this.designacao = designacao;
	}

	public String getDesignacao(){
		return designacao;
	}

	public String getSubcategoria(){
		return null;
	}

	abstract public boolean getObrigatoria();
}
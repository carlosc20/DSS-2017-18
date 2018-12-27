package business.venda.categorias;

import business.venda.categorias.Categoria;

public abstract class CategoriaOpcional extends Categoria {

    public boolean getObrigatoria(){
        return false;
    }
}
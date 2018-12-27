package business.venda.categorias;

import business.venda.categorias.Categoria;

public abstract class CategoriaObrigatoria extends Categoria {

    public boolean getObrigatoria(){
        return true;
    }
}
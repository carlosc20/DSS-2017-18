package business.venda.categorias;

import business.venda.categorias.Categoria;

public class CategoriaObrigatoria extends Categoria {

    public CategoriaObrigatoria(String designacao){
        super(designacao);
    }

    public boolean getObrigatoria(){
        return true;
    }
}
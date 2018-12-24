package business.venda.categorias;

import business.venda.categorias.Categoria;

public class CategoriaOpcional extends Categoria {

    public CategoriaOpcional(String designacao) {
        super(designacao);
    }

    public boolean getObrigatoria(){
        return false;
    }
}
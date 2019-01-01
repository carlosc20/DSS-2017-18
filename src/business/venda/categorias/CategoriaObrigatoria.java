package business.venda.categorias;

import business.venda.categorias.Categoria;

import java.util.HashMap;
import java.util.Map;

public abstract class CategoriaObrigatoria extends Categoria {

    public boolean getObrigatoria(){
        return true;
    }

}
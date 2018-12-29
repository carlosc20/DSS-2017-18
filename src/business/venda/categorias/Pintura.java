package business.venda.categorias;

import business.venda.categorias.CategoriaObrigatoria;

public class Pintura extends CategoriaObrigatoria {
    public String getDesignacao() {
        return "Pintura";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Pintura p = (Pintura) o;
        return (this.getDesignacao().equals(p.getDesignacao()));
    }
}
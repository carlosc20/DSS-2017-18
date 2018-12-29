package business.venda.categorias;

import business.venda.categorias.CategoriaObrigatoria;

public class Jantes extends CategoriaObrigatoria {
    @Override
    public String getDesignacao() {
        return "Jantes";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Jantes j = (Jantes) o;
        return (this.getDesignacao().equals(j.getDesignacao()));
    }
}
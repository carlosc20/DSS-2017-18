package business.venda.categorias;

import business.venda.categorias.CategoriaObrigatoria;

public class Motor extends CategoriaObrigatoria {
    @Override
    public String getDesignacao() {
        return "Motor";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Motor m = (Motor) o;
        return (this.getDesignacao().equals(m.getDesignacao()));
    }
}
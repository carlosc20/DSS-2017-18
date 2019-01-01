package business.venda.categorias;

public class CintoAmarelo extends CategoriaOpcional {
    public String getDesignacao() {
        return "CintoAmarelo";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        CintoAmarelo c = (CintoAmarelo) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}


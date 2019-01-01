package business.venda.categorias;

public class RadioTatil7 extends CategoriaOpcional {
    public String getDesignacao() {
        return "RadioTatil7";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        RadioTatil7 c = (RadioTatil7) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}


package business.venda.categorias;

public class RadioTatil5 extends CategoriaOpcional {
    public String getDesignacao() {
        return "RadioTatil5";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        RadioTatil5 c = (RadioTatil5) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

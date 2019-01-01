package business.venda.categorias;

public class Tablier extends CategoriaOpcional {
    public String getDesignacao() {
        return "Tablier";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Tablier c = (Tablier) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

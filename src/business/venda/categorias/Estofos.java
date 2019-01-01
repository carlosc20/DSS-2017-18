package business.venda.categorias;

public class Estofos extends CategoriaOpcional {
    public String getDesignacao() {
        return "Estofos";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Farois c = (Farois) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

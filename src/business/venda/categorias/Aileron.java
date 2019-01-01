package business.venda.categorias;

public class Aileron extends CategoriaOpcional {
    public String getDesignacao() {
        return "Aileron";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Aileron c = (Aileron) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

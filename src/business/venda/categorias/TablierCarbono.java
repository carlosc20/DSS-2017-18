package business.venda.categorias;

public class TablierCarbono extends CategoriaOpcional {
    public String getDesignacao() {
        return "TablierCarbono";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        TablierCarbono c = (TablierCarbono) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

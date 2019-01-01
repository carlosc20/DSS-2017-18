package business.venda.categorias;

public class FaroisXenon extends CategoriaOpcional {
    public String getDesignacao() {
        return "FaroisXenon";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        FaroisXenon c = (FaroisXenon) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

package business.venda.categorias;

public class EstofosPeleVermelha extends CategoriaOpcional {
    public String getDesignacao() {
        return "EstofosPeleVermelha";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        EstofosPeleVermelha c = (EstofosPeleVermelha) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}


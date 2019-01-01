package business.venda.categorias;

public class EstofosPelePreto extends CategoriaOpcional {
    public String getDesignacao() {
        return "EstofosPelePreto";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        EstofosPelePreto c = (EstofosPelePreto) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

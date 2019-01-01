package business.venda.categorias;

public class Teto extends CategoriaOpcional {
    public String getDesignacao() {
        return "Teto";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Teto c = (Teto) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}


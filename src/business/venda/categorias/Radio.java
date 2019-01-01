package business.venda.categorias;

public class Radio extends CategoriaOpcional {
    public String getDesignacao() {
        return "Rádio";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Radio c = (Radio) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}

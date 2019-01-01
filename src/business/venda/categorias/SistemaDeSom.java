package business.venda.categorias;

public class SistemaDeSom extends CategoriaOpcional {
    public String getDesignacao() {
        return "Sistema de som";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        SistemaDeSom c = (SistemaDeSom) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}


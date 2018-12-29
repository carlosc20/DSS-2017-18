package business.venda.categorias;

public class Carrocaria extends CategoriaObrigatoria {
    public String getDesignacao() {
        return "Carrocaria";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Carrocaria c = (Carrocaria) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}
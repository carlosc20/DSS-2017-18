package business.venda.categorias;

public class Pneus extends CategoriaObrigatoria {
    public String getDesignacao() {
        return "Pneus";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        Pneus p = (Pneus) o;
        return (this.getDesignacao().equals(p.getDesignacao()));
    }

}
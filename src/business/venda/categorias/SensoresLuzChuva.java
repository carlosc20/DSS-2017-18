package business.venda.categorias;

public class SensoresLuzChuva extends CategoriaOpcional {
    public String getDesignacao() {
        return "SensoresLuzLuva";
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if ((o==null) || (this.getClass() != o.getClass()))
            return false;

        SensoresLuzChuva c = (SensoresLuzChuva) o;
        return (this.getDesignacao().equals(c.getDesignacao()));
    }
}


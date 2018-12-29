package business.venda.categorias;

import business.venda.categorias.Categoria;

public class CategoriaOpcional extends Categoria {
    private String designacao;

    public CategoriaOpcional(String designacao) {
        this.designacao = designacao;
    }

    public String getDesignacao() {
        return designacao;
    }

    public boolean getObrigatoria(){
        return false;
    }
}
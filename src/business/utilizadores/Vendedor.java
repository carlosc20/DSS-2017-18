package business.utilizadores;

import business.utilizadores.Utilizador;

public class Vendedor extends Utilizador {
    public Vendedor(){
        super();
    }

    public Vendedor(String nome, String pass){
        super(nome,pass);
    }

    public Vendedor(Utilizador u){
        super(u);
    }

    public String getFuncao() {
        return "Vendedor";
    }
}

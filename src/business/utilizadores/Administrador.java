package business.utilizadores;

public class Administrador extends Utilizador {
    public Administrador(){
        super();
    }

    public Administrador(String nome, String pass){
        super(nome,pass);
    }

    public Administrador(Utilizador u){
        super(u);
    }

    public String getFuncao() {
        return "administrador";
    }
}

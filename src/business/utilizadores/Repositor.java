package business.utilizadores;

public class Repositor extends Utilizador {
    public Repositor(){
        super();
    }
    public Repositor(String nome, String pass){
        super(nome,pass);
    }

    public Repositor(Utilizador u){
        super(u);
    }

    public String getFuncao() {
        return "Repositor";
    }
}

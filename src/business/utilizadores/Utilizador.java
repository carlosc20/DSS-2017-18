package business.utilizadores;

public class Utilizador {
    private String nome;
    private String password;


    public Utilizador(){
        this.nome = "";
        this.password = "";
    }

    public Utilizador(String nome, String password){
        this.nome = nome;
        this.password = password;
    }

    public Utilizador(Utilizador u){
        this.nome = u.getNome();
        this.password = u.getPassword();
    }

    public String getNome() {
        return nome;
    }

    public String getPassword() {
        return password;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

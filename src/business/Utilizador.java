package business;

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

    private String getNome() {
        return nome;
    }

    private String getPassword() {
        return password;
    }

    private void setNome(String nome) {
        this.nome = nome;
    }

    private void setPassword(String password) {
        this.password = password;
    }
}

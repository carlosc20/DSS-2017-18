package business.venda.excecoes;

public class ComponenteNaoExisteNaConfiguracao extends Exception {
    public ComponenteNaoExisteNaConfiguracao(String message){
        super(message);
    }
}

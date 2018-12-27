package business.venda.categorias;

public class CategoriaNaoExisteException extends Exception {
    public CategoriaNaoExisteException(String message){
        super(message);
    }
}

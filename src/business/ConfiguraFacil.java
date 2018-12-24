package business;// import Venda.Encomenda;
// import Diagrama_de_packages.Business.Encomenda;

import business.utilizadores.Administrador;
import business.utilizadores.Repositor;
import business.utilizadores.Utilizador;
import business.utilizadores.Vendedor;
import javafx.collections.ObservableArrayBase;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

public class ConfiguraFacil extends Observable {

    private static ConfiguraFacil instancia = new ConfiguraFacil();
	/*
	private Utilizador utilizadorAtual;
	private Venda.Encomenda encomendaAtual;
	private CategoriaDAO categorias;
	private EncomendaEmProducaoDAO filaProducao;
	private ComponenteDAO todosComponentes;
	private PacoteDAO todosPacotes;
	private EncomendaDAO encomendas; // nome corrigido
	private UtilizadorDAO utilizadores;
	*/

    public static ConfiguraFacil getInstancia() {
        return instancia;
    }

    private ConfiguraFacil() {
    }

    public int autenticar(String nome, String password) throws Exception {
        if (nome.equals("administrador")) return 0;
        if (nome.equals("vendedor")) return 1;
        if (nome.equals("repositor")) return 2;
        throw new Exception();
    }

    public void criarEncomenda(String cliente, int nif) throws Exception { //muda nome
        throw new UnsupportedOperationException();
    }

    public List<Integer> adicionaComponente(int aIdComponente) {
        throw new UnsupportedOperationException();
    }

    public void removeComponente(int aId) {
        throw new UnsupportedOperationException();
    }

    public List<Integer> adicionaPacote(int aId) {
        throw new UnsupportedOperationException();
    }

    public void removePacote(int aId) {
        throw new UnsupportedOperationException();
    }

    public void criarConfiguracaoOtima() { // muda nome
        throw new UnsupportedOperationException();
    }

    public List<Integer> finalizarEncomenda() { // muda nome, devolve pacotes formados
        throw new UnsupportedOperationException();
    }



    public void consultarConfiguracao() {
        throw new UnsupportedOperationException();
    }

    public void consultarRegistoProduzidas() {
        throw new UnsupportedOperationException();
    }

    public void consultarFilaProducao() {
        throw new UnsupportedOperationException();
    }



    public void atualizarStock(File file) throws Exception { // mudou nome, mudou tipo argumento, manda exception
        setChanged();
        notifyObservers();
        throw new UnsupportedOperationException();
    }

    public void consultarStock() { // TODO: separar em componentes e pacotes
        throw new UnsupportedOperationException();
    }



    public List<String> getFuncionarios() { // novo
        return gajos;
    }

    public List<String> getTiposFuncionarios() { // novo
        String[] tipos = {"Administrador", "Repositor", "Vendedor"};
        return Arrays.asList(tipos);
    }

    private String[] gajosa = {"Ângelo", "Carlos", "Daniel", "Marco"}; // TODO: apagar quando DAO estiver feito
    private ArrayList<String> gajos = new ArrayList<>(Arrays.asList(gajosa));

    /**
     * Cria um utilizador no sistema e adiciona-o à base de dados
     */
    public void criarUtilizador(String nome, String password, String tipo) {

        Utilizador u;

        switch (tipo) {
            case "Vendedor":
                u = new Vendedor(nome, password);
            case "Administrador":
                u = new Administrador(nome, password);
            case "Repositor":
                u = new Repositor(nome, password);
            default:
                u = new Utilizador(nome, password);
        }

        gajos.add(nome);
        setChanged();
        notifyObservers();

    }

    /**
     * Remove um utilizador do sistema
     */
    public void removerUtilizador(String nome) {
        gajos.remove(nome);
        setChanged();
        notifyObservers();
	    /*
		Utilizador u = utilizadores.get(nome); //utilizadores -> UtilizadorDAO;
		utilizadores.delete(u);
		*/
    }

	/*
	private void colocaNaFila(Diagrama_de_packages.Business.Encomenda aEncomendaAtual, List<Integer> aEmFalta) {
		throw new UnsupportedOperationException();
	}
	*/

}
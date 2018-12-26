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


    // -------------------------------- Encomenda ------------------------------------------

    public void criarEncomenda(String cliente, int nif) throws Exception { //muda nome

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


    // TODO: 26/12/2018 acabar
    public Object[][] getRegistoProduzidas() { //novo
        return null;
    }

    public String[] getColunasRegistoProduzidas() { //novo
        String[] columnNames = {
                "Id",
                "Cliente",
                "Nif",
                "Preço sem descontos (€)",
                "Descontos (€)",
                "Componentes",
                "Pacotes"
        };
        return columnNames;
    }

    public Object[][] getFilaProducao() { //novo
        return null;
    }

    public String[] getColunasFilaProducao() { //novo
        String[] columnNames = {
                "Id",
                "Componentes em falta"
        };
        return columnNames;
    }


    // -------------------------------- Stock -----------------------------------------------

    /**
     * Substitui o stock atual pelo fornecido num ficheiro CSV
     *
     * @param file ficheiro de formato CSV que contém as informações de stock (componentes e pacotes)
     */
    public void atualizarStock(File file) throws Exception { // mudou nome, mudou tipo argumento, manda exception


        setChanged();
        notifyObservers();
    }

    // TODO: 26/12/2018 acabar
    public Object[][] getComponentes() { //novo

        Object[][] data = {
                {1, "Motor",
                        "Opel V6", 1, 100},
                {2, "Motor",
                        "BMW X31", 3, 200},
                {3, "Pneus",
                        "Goodyear LT235", 2, 400},
                {4, "Pintura",
                        "Vermelho gloss", 20, 500},
                {5, "Jantes",
                        "Metal XMZ",10, 400}
        };
        return data;
    }

    public String[] getColunasComponentes() {  //novo
        String[] columnNames = {
                "Id",
                "Categoria",
                "Designação",
                "Qtd em stock",
                "Preço(€)"
        };
        return columnNames;
    }

    public Object[][] getPacotes() {  //novo
        return null;
    }

    public String[] getColunasPacotes() {  //novo
        String[] columnNames = {
                "Id",
                "Designação",
                "Desconto(€)",
                "Componentes"
        };
        return columnNames;
    }


    // -------------------------------- Utilizadores ----------------------------------------

    /**
     * Devolve o cargo do funcionário correspondente às credencias fornecidas.
     *
     * @return 0 se for administrador, 1 se for vendedor, 2 se for repositor
     */
    public int autenticar(String nome, String password) throws Exception {
        if (nome.equals("administrador")) return 0;
        if (nome.equals("vendedor")) return 1;
        if (nome.equals("repositor")) return 2;
        throw new Exception();
    }

    private String[] gajosa = {"Ângelo", "Carlos", "Daniel", "Marco"}; // TODO: apagar quando DAO estiver feito
    private ArrayList<String> gajos = new ArrayList<>(Arrays.asList(gajosa));

    /**
     * Devolve uma lista com os nomes dos funcionários existentes.
     */
    public List<String> getFuncionarios() { // novo
        return gajos;
    }


    /**
     * Devolve uma lista com os tipos de funcionários existentes.
     */
    public List<String> getTiposFuncionarios() { // novo
        String[] tipos = {"Administrador", "Repositor", "Vendedor"};
        return Arrays.asList(tipos);
    }

    /**
     * Cria um utilizador no sistema e adiciona-o à base de dados
     */
    public void criarUtilizador(String nome, String password, String tipo) {

        gajos.add(nome);
        /*
        Utilizador u;
        switch (tipo) {
            case "Vendedor":
                u = new Vendedor(nome, password);
            case "Administrador":
                u = new Administrador(nome, password);
            case "Repositor":
                u = new Repositor(nome, password);
        }
        DAO.add(u);
        */
        setChanged();
        notifyObservers();
    }


    /**
     * Remove um utilizador do sistema
     */
    public void removerUtilizador(String nome) {

        gajos.remove(nome);
        // TODO: n da com chave so no DAO?
        /*
		Utilizador u = DAO.get(nome);
		DAO.remove()
		*/
        setChanged();
        notifyObservers();
    }

	/*
	private void colocaNaFila(Diagrama_de_packages.Business.Encomenda aEncomendaAtual, List<Integer> aEmFalta) {
		throw new UnsupportedOperationException();
	}
	*/

}
package business;// import Venda.Encomenda;
// import Diagrama_de_packages.Business.Encomenda;

import business.produtos.Componente;
import business.utilizadores.Administrador;
import business.utilizadores.Repositor;
import business.utilizadores.Utilizador;
import business.utilizadores.Vendedor;
import business.venda.*;
import business.venda.categorias.Categoria;
import data.*;
import javafx.collections.ObservableArrayBase;
import javafx.util.Pair;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class ConfiguraFacil extends Observable {

    private static ConfiguraFacil instancia = new ConfiguraFacil();
	private Utilizador utilizadorAtual;
	private Encomenda encomendaAtual;
	private CategoriaDAO categorias;
	private EncomendaEmProducaoDAO filaProducao;
	private ComponenteDAO todosComponentes;
	private PacoteDAO todosPacotes;
	private EncomendaDAO encomendas; // nome corrigido
	private UtilizadorDAO utilizadores;


    public static ConfiguraFacil getInstancia() {
        return instancia;
    }

    private ConfiguraFacil() {
    }


    // -------------------------------- Encomenda ------------------------------------------
    //falta o dao
    public void criarEncomenda(String cliente, int nif) throws Exception { //muda nome
        encomendaAtual = new Encomenda(1,cliente, nif, todosComponentes, todosPacotes);
    }

    public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarComponente(int idComponente) throws ComponenteJaExisteNaConfiguracaoException, SQLException{
        return encomendaAtual.getEfeitosAdicionarComponente(idComponente);
    }

    public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException, SQLException{
            return this.encomendaAtual.getEfeitosAdicionarPacote(idPacote);
    }

    public Set<Integer> adicionaComponente(int idComponente) throws SQLException {
            return encomendaAtual.adicionaComponente(idComponente);
    }

    public Set<Integer> removeComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao {
        Set<Integer> res = new HashSet<>();
        try {
            res = encomendaAtual.removeComponente(idComponente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Set<Integer> adicionaPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException {
        Set<Integer> res = new HashSet<>();
        try {
            res = encomendaAtual.adicionaPacote(idPacote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void removePacote(int idPacote) throws PacoteNaoExisteNaConfiguracaoException {
        try {
          encomendaAtual.removePacote(idPacote);
        }catch (SQLException e) {
            e.printStackTrace();
        }
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

    //fazer no encomendaDAO
    // TODO: 26/12/2018 acabar
    public Object[][] getRegistoProduzidas() { //novo
       // return encomendas.getRegistoProduzidas();
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

    /**
     *
     * @return Object[][] com todos os componentes no formato
     * {id,Designação da categoria,designacao da componente,quantidade,preço}
     */
    // TODO: Precisa de ser testado depois dos DAOs estarem feitos
    public Object[][] getComponentes(){
       /*
        Set<Componente> componentes = todosComponentes.set();
        Object[componentes.size()][5] componentesTodas = new Object();
        int i = 0;
        for(Componente c : componentes){
            int id = c.getId();
            String designacao = c.getDesignacao();
            Categoria cat = c.getCategoria();
            String catDesignacao = cat.getDesignacao();
            int qnt = c.getStock();
            int preco = c.getPreco();
            componentesTodas[i] = {id,catDesignacao,designacao,qnt,preco};
            i++;
        }
        return componentesTodas;
    */
       return null;
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

    /**
     *
     * @return Object [][] com todos os Pacotes no formato {id,designacao do pacote}
     */
    //Precisa de ser testado depois dos DAOs
    //TODO: Precisa de ser feito
    public Object[][] getPacotes() {
        /*
        List pacotes = todosPacotes.list();
        Object[pacotes.size()][2] pacotesTodos;
        int i = 0;
        for(Pacote p : pacotes){
            int id = p.getId();
            String designacao = p.getDesignacao();
            pacotesTodos[i] = {id,designacao};
            i++;
        }*/
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
     * @return List<nomes:String>
     */
    // TODO: Precisa de ser feito
    public List<String> getFuncionarios() {
        /*
        List users = utilizadores.list();
        List<String> nomes = new ArrayList<>();
        for(Utilizador u: users){
            String nome = u.getNome();
            nomes.add(nome);
        }
        return nomes;*/
        return null;
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
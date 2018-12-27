package business;// import Venda.Encomenda;
// import Diagrama_de_packages.Business.Encomenda;

import business.produtos.Componente;
import business.produtos.Pacote;
import business.utilizadores.Administrador;
import business.utilizadores.Repositor;
import business.utilizadores.Utilizador;
import business.utilizadores.Vendedor;
import business.venda.*;
import business.venda.categorias.CategoriaObrigatoria;
import data.*;
import business.venda.Encomenda;
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
	private UtilizadorDAO utilizadores = new UtilizadorDAO();


    public static ConfiguraFacil getInstancia() {
        return instancia;
    }

    private ConfiguraFacil() {
    }

    // -------------------------------- Encomenda ------------------------------------------

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

    // -------------------------------- Encomenda atual ----------------------------------------------------------------
    public void criarEncomenda(String cliente, int nif) throws Exception { //muda nome
        encomendaAtual = new Encomenda(1,cliente, nif, todosComponentes, todosPacotes);
    }

    public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarComponente(int idComponente) throws ComponenteJaExisteNaConfiguracaoException{
        try {
            return encomendaAtual.getEfeitosAdicionarComponente(idComponente);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException{
        try {
            return this.encomendaAtual.getEfeitosAdicionarPacote(idPacote);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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


    /**
     * asdsa
     *
     * @return tabela com uma linha por cada categoria obrigatória
     */
    public Object [][] getComponentesObgConfig() { //novo
/*
        Object[][] data = {
                {"Motor", null, null, null, null},
                {"Motor", null, null, null, null},
                {"Motor", null, null, null, null},
                {"Motor", null, null, null, null},
                {"Motor", null, null, null, null}
        };*/
        List<Categoria> categ = new ArrayList<>();
        try {
            categ = categorias.list();
            } catch (SQLException e) {
            e.printStackTrace();
        }
        Set<Componente> comp = encomendaAtual.getComponentes();
        if (categ.size() == 0) return null;

        Object[][] data = buildCategObirgatorias(categ);
        for(int i = 0; i<data.length; i++)
            for(Componente c : comp) {
                if (c.getCategoria().getDesignacao().equals(data[i][0])) {
                    data[i][1] = new Object[]{c.getId(), c.getDesignacao(), c.getStock(), c.getPreco()};
                }
            }
        return data;
    }

        private Object[][] buildCategObirgatorias (List<Categoria> categ) {
            Object[][] data = new Object[categ.size()][5];
            int i = 0;
            for (Categoria cat : categ) {
                String des = cat.getDesignacao();
                if (cat instanceof CategoriaObrigatoria) {
                    data[i] = new Object[]{cat.getDesignacao(), null, null, null, null};
                    i++;
                }
            }
            return data;
        }


    // -------------------------------- Stock --------------------------------------------------------------------------

    /**
     * Substitui o stock atual pelo fornecido num ficheiro CSV
     *
     * @param file ficheiro de formato CSV que contém as informações de stock (componentes e pacotes)
     */
    public void atualizarStock(File file) throws Exception { // mudou nome, mudou tipo argumento, manda exception

        // TODO: 27/12/2018 fazer

        setChanged();
        notifyObservers();
    }

    /**
     *
     * @return Object[][] com todos os componentes no formato
     * {id,Designação da categoria,designacao da componente,quantidade,preço}
     */
    // Feito mas precisa de ser testado


    public Object[][] getComponentes(){
        List<Componente> componentes = new ArrayList<>();
        try {
            componentes = todosComponentes.list();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (componentes.size()==0) return null;

        Object[][] componentesTodas = new Object[componentes.size()][5];
        int i = 0;
        for(Componente c : componentes){
            int id = c.getId();
            String designacao = c.getDesignacao();
            Categoria cat = c.getCategoria();
            String catDesignacao = cat.getDesignacao();
            int qnt = c.getStock();
            int preco = c.getPreco();
            componentesTodas[i] = new Object[]{id,catDesignacao,designacao,qnt,preco};
            i++;
        }
        return componentesTodas;

     //  return null;
    }

    /**
     * Faz coisas.
     *
     * @return matriz de objetos com todos os Pacotes no formato {id,designacao do pacote}
     */
    public Object[][] getComponentes(String categoria) { //novo

        Object[][] data = {
                {"Motor", 1, "Opel V6", 1, 100},
                {"Motor", 2, "BMW X31", 3, 200}
        };
        return data;
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getComponentes()}. */
    public static String[] colunasComponentes = new String[] {"Categoria", "Id", "Designação", "Qtd em stock", "Preço(€)"};


    /**
     * Faz coisas.
     *
     * @return Object [][] com todos os Pacotes no formato {id,designacao do pacote}
     */
    //Feito mas precisa de ser testado
    public Object[][] getPacotes() {
        List<Pacote> pacotes = new ArrayList<>();
        try {
            pacotes = todosPacotes.list();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (pacotes.size() == 0) return null;
        Object[][] pacotesTodos = new Object[pacotes.size()][2];

        int i = 0;
        for(Pacote p : pacotes){
            int id = p.getId();
            String designacao = p.getDesignacao();
            pacotesTodos[i] = new Object[]{id,designacao};
            i++;
        }
        return null;
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getPacotes()}. */
    public static String[] colunasPacotes = new String[] {"Id", "Designação", "Desconto(€)", "Componentes"};


    /**
     * Devolve uma lista de todas as categorias de componentes obrigatórios.
     *
     * @return lista de categorias
     */
    public List<String> getCategoriasObrigatorias() {
        String[] c = {"cat 1", "cat 2", "cat 3"};
        return Arrays.asList(c);
    }


    /**
     * Devolve uma lista de todas as categorias de componentes opcionais.
     *
     * @return lista de categorias
     */
    public List<String> getCategoriasOpcionais() {
        String[] c = {"cat 1", "cat 2", "cat 3"};
        return Arrays.asList(c);
    }


    // -------------------------------- Utilizadores -------------------------------------------------------------------

    /**
     * Devolve o cargo do funcionário correspondente às credencias fornecidas.
     *
     * @param nome nome do utilizador
     * @param password password do utilizador
     *
     * @return 0 se for administrador, 1 se for vendedor, 2 se for repositor
     */
    public String autenticar(String nome, String password) throws Exception {
        // TODO: tirar na versão final
        if (nome.equals("administrador")) return "administrador";
        if (nome.equals("vendedor")) return "vendedor";
        if (nome.equals("repositor")) return "repositor";

        Utilizador u = utilizadores.get(nome);
        if (u.getPassword().equals(password)) {
            return u.getFuncao();
        }

        throw new Exception();
    }

    /**
     * Devolve uma lista com os nomes dos funcionários existentes.
     *
     * @return lista com os nomes dos funcionários
     */
    public List<String> getFuncionarios() throws Exception {
        try {
            List<Utilizador> users = utilizadores.list();
            List<String> nomes = new ArrayList<>(users.size());
            for(Utilizador u: users){
                String nome = u.getNome();
                nomes.add(nome);
            }
            return nomes;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }


    /** Array com todos os tipos possíveis de funcionário. */
    public static String[] tiposUtilizadores = new String[] {"Administrador", "Repositor", "Vendedor"};


    /**
     * Cria um utilizador no sistema e adiciona-o à base de dados.
     *
     * @param nome      nome do utilizador
     * @param password  password do utilizador
     * @param tipo      tipo do utilizador
     */
    public void criarUtilizador(String nome, String password, String tipo) throws Exception {
        // TODO: 27/12/2018 por restriçoes ao nome e pass, melhorar a exceçao
        Utilizador u;
        switch (tipo) {
            case "Vendedor":
                u = new Vendedor(nome, password);
                break;
            case "Administrador":
                u = new Administrador(nome, password);
                break;
            case "Repositor":
                u = new Repositor(nome, password);
                break;
                default:
                    throw new Exception();
        }
        utilizadores.add(u);
        setChanged();
        notifyObservers();
    }


    /**
     * Remove um utilizador do sistema.
     *
     * @param nome  nome do utilizador
     */
    public void removerUtilizador(String nome) {
        try {
            utilizadores.remove(nome);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }
}
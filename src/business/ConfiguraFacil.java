package business;// import Venda.Encomenda;
// import Diagrama_de_packages.Business.Encomenda;

import business.gestao.Encomenda;
import business.gestao.EncomendaEmProducao;
import business.produtos.Componente;
import business.produtos.Pacote;
import business.utilizadores.Administrador;
import business.utilizadores.Repositor;
import business.utilizadores.Utilizador;
import business.utilizadores.Vendedor;
import business.venda.*;
import business.venda.categorias.*;
import data.*;
import business.venda.EncomendaAtual;
import business.venda.categorias.Categoria;
import javafx.util.Pair;

import java.io.File;
import java.sql.SQLException;
import java.util.*;

public class ConfiguraFacil extends Observable {

    private static ConfiguraFacil instancia = new ConfiguraFacil();

	private Utilizador utilizadorAtual;
	private EncomendaAtual encomendaAtual;

	private EncomendaEmProducaoDAO filaProducao = new EncomendaEmProducaoDAO();
	private ComponenteDAO todosComponentes = new ComponenteDAO();
	private PacoteDAO todosPacotes = new PacoteDAO();
	private EncomendaDAO registoProduzidas = new EncomendaDAO();
	private CategoriaDAO categorias = new CategoriaDAO();
	private UtilizadorDAO utilizadores = new UtilizadorDAO();


    public static ConfiguraFacil getInstancia() {
        return instancia;
    }

    private ConfiguraFacil(){}

    // -------------------------------- Encomendas ---------------------------------------------------------------------

    /**
     *  Devolve uma matriz com informações das encomendas no registo de encomendas produzidas.
     *  Cada linha corresponde a uma encomenda.
     *
     *  @return
     */
    public Object[][] getRegistoProduzidas() throws Exception { //novo
        try {
            List<Encomenda> encs = registoProduzidas.list();
            Object[][] data = new Object[encs.size()][ConfiguraFacil.colunasRegistoProduzidas.length];
            int i = 0;
            for (Encomenda e : encs) {
                int id = e.getId();

                data[i] = new Object[]{id, };
                i++;
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getRegistoProduzidas()}. */
    public static String[] colunasRegistoProduzidas = new String[] {
            "Id",
            "Cliente",
            "Nif",
            "Preço sem descontos (€)",
            "Descontos (€)",
            "Componentes",
            "Pacotes"
    };


    /**
     *  Devolve uma matriz com informações das encomendas na fila de encomendas em produção.
     *  Cada linha corresponde a uma encomenda.
     *
     *  @return
     */
    public Object[][] getFilaProducao() throws Exception { //novo
        try {
            List<EncomendaEmProducao> encs = filaProducao.list();
            Object[][] data = new Object[encs.size()][ConfiguraFacil.colunasFilaProducao.length];
            int i = 0;
            for (EncomendaEmProducao e : encs) {
                int id = e.getId();
                Collection<Componente> comps = e.getComponentesEmFalta();
                String c = comps.toString(); // TODO: 27/12/2018 completar
                data[i] = new Object[]{id, c};
                i++;
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getFilaProducao()}. */
    public static String[] colunasFilaProducao = new String[] {"Id", "Componentes em falta"};

    // -------------------------------- Encomenda Atual ----------------------------------------------------------------

    public void criarEncomenda(String cliente, int nif) throws Exception { //muda nome
        encomendaAtual = new EncomendaAtual(1,cliente, nif);
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

        List<Categoria> categ = new ArrayList<>();
        try {
            categ = categorias.list();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (CategoriaNaoExisteException categoriaNaoExiste) {
            try {
                categorias.remove(categoriaNaoExiste.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        Set<Componente> comp = encomendaAtual.getComponentes();
        if (categ.size() == 0) return null;

        Object[][] data = buildCategObrigatorias(categ);
        for(int i = 0; i<data.length; i++)
            for(Componente c : comp) {
                if (c.getCategoria().getDesignacao().equals(data[i][0])) {
                    data[i][1] = new Object[]{c.getId(), c.getDesignacao(), c.getStock(), c.getPreco()};
                }
            }
        return data;
    }

        private Object[][] buildCategObrigatorias (List<Categoria> categ) {
            Object[][] data = new Object[categ.size()][5];
            int i = 0;
            for (Categoria cat : categ) {
                String des = cat.getDesignacao();
                if (cat.getObrigatoria()) {
                    data[i] = new Object[]{des, null, null, null, null};
                    i++;
                }
            }
            return data;
        }

    public Object [][] getComponentesOpcConfig() {
        return new Object[][] {
                {"Motor", 1, "Teste", 1, 20},
        };
    }

    public Object [][] getComponentesDepConfig() {
        return new Object[][] {
                {"Motor", 1, "Teste", 1, 20},
        };
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
     * @param categoria categoria dos componentes
     *
     * @return matriz de objetos com todos os Pacotes no formato {id,designacao do pacote}
     */
    public Object[][] getComponentes(String categoria) { //novo
        Categoria cat = null;
        switch (categoria){
            case "Carrocaria":
                cat = new Carrocaria();
                break;
            case "Jantes":
                cat = new Jantes();
                break;
            case "Motor":
                cat = new Motor();
                break;
            case "Pintura":
                cat = new Pintura();
                break;
            case "Pneus":
                cat = new Pneus();
                break;
            default: return null;
        }

        List<Componente> componentes = new ArrayList<>();
        try {
            componentes = todosComponentes.list(cat);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (componentes.size()==0) return null;

        Object[][] componentesTodas = new Object[componentes.size()][5];
        int i = 0;
        for(Componente c : componentes){
            int id = c.getId();
            String designacao = c.getDesignacao();
            Categoria cate = c.getCategoria();
            String catDesignacao = cate.getDesignacao();
            int qnt = c.getStock();
            int preco = c.getPreco();
            componentesTodas[i] = new Object[]{id,catDesignacao,designacao,qnt,preco};
            i++;
        }
        return componentesTodas;
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
        return pacotesTodos;
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getPacotes()}. */
    public static String[] colunasPacotes = new String[] {"Id", "Designação", "Desconto(€)", "Componentes"};


    /**
     * Devolve uma lista de todas as categorias de componentes obrigatórios.
     *
     * @return lista de categorias
     */
    // feito precisa de ser testado
    public List<String> getCategoriasObrigatorias() {

        List<Categoria> cats = new ArrayList<>();

        try {
            cats = categorias.list();
        } catch (CategoriaNaoExisteException | SQLException e) {
            e.printStackTrace();
        }

        if(cats.size()==0) return null;

        List<String> nomes = new ArrayList<>();

        for(Categoria c : cats){
            if(c.getObrigatoria()){
                nomes.add(c.getDesignacao());
            }
        }
        return nomes;
    }


    /**
     * Devolve uma lista de todas as categorias de componentes opcionais.
     *
     * @return lista de categorias
     */
    // feito precisa de ser testado
    public List<String> getCategoriasOpcionais() {
        List<Categoria> cats = new ArrayList<>();

        try {
            cats = categorias.list();
        } catch (CategoriaNaoExisteException | SQLException e) {
            e.printStackTrace();
        }

        if(cats.size()==0) return null;

        List<String> nomes = new ArrayList<>();

        for(Categoria c : cats){
            if(!c.getObrigatoria()){
                nomes.add(c.getDesignacao());
            }
        }
        return nomes;
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
        if (nome.equals("Administrador")) return "Administrador";
        if (nome.equals("Vendedor")) return "Vendedor";
        if (nome.equals("Repositor")) return "Repositor";

        Utilizador u = utilizadores.get(nome);
        String pw = u.getPassword();
        if (pw.equals(password)) {
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
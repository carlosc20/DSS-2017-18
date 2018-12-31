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


    // -------------------------------- Encomenda Atual ----------------------------------------------------------------

    /**
     * Cria uma encomenda com os dados do cliente
     *
     * @param cliente   nome do cliente
     * @param nif       nif do cliente
     * @throws Exception
     */
    public void criarEncomenda(String cliente, int nif) throws Exception { //muda nome
        encomendaAtual = new EncomendaAtual(1,cliente, nif);
    }


    public Componente getC (int id){
        try {
            return todosComponentes.get(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Devolve um par com dois Sets de ids de componentes associados à adição de um componente.
     * O primeiro Set tem os ids dos componentes incompatíveis.
     * O segundo Set tem os ids dos componentes de que o componente adicionado depende.
     *
     * @param idComponente   id do componente a adicionar
     *
     * @return  Par com Sets de ids de componentes
     */
    public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarComponente(int idComponente) throws ComponenteJaExisteNaConfiguracaoException{
        try {
            Pair<Set<Integer>,Set<Integer>> r = encomendaAtual.getEfeitosAdicionarComponente(idComponente);
            setChanged();
            notifyObservers();
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Devolve um par com dois Sets de ids de componentes associados à adição de um pacote.
     * O primeiro Set tem os ids dos componentes incompatíveis.
     * O segundo Set tem os ids dos componentes de que o pacote adicionado depende.
     *
     * @param idPacote  id do pacote a adicionar
     * @return
     * @throws PacoteJaExisteNaConfiguracaoException
     * @throws PacoteGeraConflitosException
     */
    public Pair<Set<Integer>,Set<Integer>> getEfeitosAdicionarPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException{
        try {
            Pair<Set<Integer>,Set<Integer>> r = this.encomendaAtual.getEfeitosAdicionarPacote(idPacote);
            setChanged();
            notifyObservers();
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<Integer> adicionaComponente(int idComponente) throws SQLException {
        Set<Integer> r = encomendaAtual.adicionaComponente(idComponente);
        setChanged();
        notifyObservers();
        return r;
    }

    public Set<Integer> removeComponente(int idComponente) throws ComponenteNaoExisteNaConfiguracao {
        Set<Integer> res = new HashSet<>();
        try {
            res = encomendaAtual.removeComponente(idComponente);
            setChanged();
            notifyObservers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Set<Integer> adicionaPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException {
        Set<Integer> res = new HashSet<>();
        try {
            res = encomendaAtual.adicionaPacote(idPacote);
            setChanged();
            notifyObservers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void removePacote(int idPacote) throws PacoteNaoExisteNaConfiguracaoException {
        try {
            encomendaAtual.removePacote(idPacote);
            setChanged();
            notifyObservers();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void criarConfiguracaoOtima() { // muda nome
        setChanged();
        notifyObservers();
        throw new UnsupportedOperationException();
    }

    /**
     * blabla
     *
     * @return lista de pacotes formados
     */
    public List<Integer> finalizarEncomenda() throws FaltamDependentesException, Exception { // muda nome, devolve pacotes formados
        try {
            Encomenda feita = encomendaAtual.finalizarEncomenda();
            if(feita.getFinalizada()) {
                registoProduzidas.add(feita);
            } else {
                filaProducao.add((EncomendaEmProducao) feita);
            }
            return new ArrayList<>(); // TODO: 29/12/2018 pacotes formados
        } catch (SQLException e){
            e.printStackTrace();
            throw new Exception(); // TODO: 29/12/2018 exceçao fixe
        }
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
        List<Componente> comp = encomendaAtual.getComponentesObrigatorios();
        Object[][] data = buildCategObrigatorias(categ);
        for(int i = 0; i<categ.size(); i++)
            for(Componente c : comp) {
                if (c.getCategoria().getDesignacao().equals(data[i][0])) {
                    data[i][1] = c.getId();
                    data[i][2] = c.getDesignacao();
                    data[i][3] = c.getStock();
                    data[i][4] = c.getPreco();
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

    /**
     * Devolve uma matriz com informações dos componentes do sistema.
     * Cada linha corresponde a um componente e as colunas a {@link #colunasComponentes}.
     *
     * @return matriz com uma linha por componente
     */
    public Object [][] getComponentesOpcConfig() {
        return componentesListToMatrix(encomendaAtual.getComponetesOpcionais());
    }


    /**
     * Devolve uma matriz com informações dos componentes dependencias da encomenda atual.
     * Cada linha corresponde a um componente e as colunas a {@link #colunasComponentes}.
     *
     * @return matriz com uma linha por componente
     */
    public Object [][] getComponentesDepConfig() throws Exception {
        Set<Integer> compIds = encomendaAtual.getDependentes();
        ArrayList<Componente> componentes = new ArrayList<>(compIds.size());
        for(int id : compIds){
            try {
                componentes.add(todosComponentes.get(id));
            } catch (SQLException e) {
                throw new Exception(); // TODO: 29/12/2018 exception fixe
            }
        }
        return componentesListToMatrix(componentes);
    }

    /**
     * Devolve uma matriz com informações dos pacotes da encomenda atual.
     * Cada linha corresponde a um pacote e as colunas a {@link #colunasPacotes}.
     *
     * @return matriz com uma linha por pacote
     */
    public Object [][] getPacotesConfig() throws Exception {
        List<Pacote> pac = encomendaAtual.getPacotes();
        Object[][] data = new Object[pac.size()][4];
        int i = 0;
        for (Pacote p : pac) {
            data[i][0] = p.getId();
            data[i][1] = p.getDesignacao();
            data[i][2] = p.getDesconto();
            data[i][3] = p.getComponentes().toString();
            i++;
        }
        return data;
    }

    /**
     * Devolve o preço da encomenda Atual
     *
     * @return inteiro com valor da encomenda
     */
    public int getValor(){
        return encomendaAtual.getValor();
    }

    /**
     * Devolve o desconto total da encomenda Atual que é formado pelos seus pacotes
     *
     * @return inteiro com valor do desconto
     */
    public int getDesconto(){
        return encomendaAtual.getDesconto();
    }

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
                data[i][0] = e.getId();
                data[i][1] = e.getCliente();
                data[i][2] = e.getNif();
                data[i][3] = e.getValor();
                data[i][4] = e.getComponentes().toString();
                data[i][5] = e.getPacotes().toString();
                i++;
            }
            return data;
        } catch (SQLException e) {
            throw new Exception();
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getRegistoProduzidas()}. */
    public static String[] colunasRegistoProduzidas = new String[] {
            "Id",
            "Cliente",
            "Nif",
            "Preço total (€)",
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
                data[i][0] = e.getId();
                data[i][1] = e.getCliente();
                data[i][2] = e.getNif();
                data[i][3] = e.getValor();
                data[i][4] = e.getComponentes().toString();
                data[i][5] = e.getPacotes().toString();
                data[i][6] = e.getComponentesEmFalta().toString();
                i++;
            }
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getFilaProducao()}. */
    public static String[] colunasFilaProducao = new String[] {
            "Id",
            "Cliente",
            "Nif",
            "Preço total (€)",
            "Componentes",
            "Pacotes",
            "Componentes em falta"
    };


    // -------------------------------- Stock --------------------------------------------------------------------------

    /**
     * Substitui os componentes atuais pelos fornecidos num ficheiro CSV
     *
     * @param file ficheiro de formato CSV que contém as informações de componentes
     */
    public void atualizaComponentes(File file) throws Exception { // mudou nome, mudou tipo argumento, manda exception
        // TODO: 29/12/2018 passar logica para aqui
        new ComponenteDAO().importCSV(file);

        setChanged();
        notifyObservers(0);
    }


    /**
     * Substitui os pacotes atuais pelos fornecidos num ficheiro CSV
     *
     * @param file ficheiro de formato CSV que contém as informações de pacotes
     */
    public void atualizaPacotes(File file) throws Exception { // mudou nome, mudou tipo argumento, manda exception
        // TODO: 29/12/2018 passar logica para aqui
        new PacoteDAO().importCSV(file);

        setChanged();
        notifyObservers(1);
    }

    /**
     * Devolve uma matriz com informações dos componentes do sistema.
     * Cada linha corresponde a um componente e as colunas a {@link #colunasComponentes}.
     *
     * @return matriz com uma linha por componente
     */
    public Object[][] getComponentes() throws Exception {
        try {
            List<Componente> componentes = todosComponentes.list();
            return componentesListToMatrix(componentes);
        } catch (SQLException e) {
            throw new Exception(); // TODO: 29/12/2018 exception fixe
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getComponentes()}. */
    public static String[] colunasComponentes = new String[] {"Categoria", "Id", "Designação",
                                                                "Qtd em stock", "Preço(€)"};



    // TODO: 29/12/2018 separar em obg e opc?
    /**
     * Faz coisas.
     *
     * @param categoria categoria dos componentes
     *
     * @return matriz de objetos com todos os Pacotes no formato {id,designacao do pacote}
     */
    public Object[][] getComponentes(String categoria) throws Exception { //novo
        Categoria cat;
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

        try {
            List<Componente> componentes = todosComponentes.list(cat);
            Object[][] componentesTodas = new Object[componentes.size()][5];
            int i = 0;
            for(Componente c : componentes){
                int id = c.getId();
                String designacao = c.getDesignacao();
                Categoria cate = c.getCategoria();
                String catDesignacao = cate.getDesignacao();
                int qnt = c.getStock();
                int preco = c.getPreco();
                componentesTodas[i] = new Object[]{catDesignacao,id,designacao,qnt,preco};
                i++;
            }
            return componentesTodas;
        } catch (SQLException e) {
            throw new Exception(); // TODO: 29/12/2018 exception fixe
        }
    }




    /**
     * Devolve uma matriz com informações dos pacotes do sistema.
     * Cada linha corresponde a um pacote e as colunas a {@link #colunasPacotes}.
     *
     * @return matriz com uma linha por pacote
     */
    public Object[][] getPacotes() throws Exception {
        try {
            List<Pacote> pacotes = todosPacotes.list();
            Object[][] data = new Object[pacotes.size()][colunasPacotes.length];
            int i = 0;
            for(Pacote p : pacotes){
                data[i][0] = p.getId();
                data[i][1] = p.getDesignacao();
                data[i][2] = p.getDesconto();
                data[i][3] = p.getComponentes().toString();
                i++;
            }
            return data;
        } catch (SQLException e) {
            throw new Exception();
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getPacotes()}. */
    public static String[] colunasPacotes = new String[] {"Id", "Designação", "Desconto(€)", "Componentes"};


    /**
     * Devolve uma lista de todas as categorias de componentes opcionais.
     *
     * @return lista de categorias
     */
    public List<String> getCategoriasOpcionais() throws Exception {
        try {
            List<String> nomes = new ArrayList<>();
            for(Categoria c : categorias.list()){
                if(!c.getObrigatoria()){
                    nomes.add(c.getDesignacao());
                }
            }
            return nomes;
        } catch (CategoriaNaoExisteException | SQLException e) {
            e.printStackTrace();
            throw new Exception(); // TODO: 29/12/2018 exception fixe
        }
    }
    /**
     * Devolve uma lista de todas as categorias de componentes opcionais.
     *
     * @return lista de categorias
     */
    // -------------------------------- Utilizadores -------------------------------------------------------------------

    /**
     * Devolve o tipo do utilizador correspondente às credencias fornecidas.
     *
     * @param nome      nome do utilizador
     * @param password  password do utilizador
     *
     * @return tipo do utilizador
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

        throw new Exception(); // TODO: 29/12/2018 dados incorretos
    }

    /**
     * Devolve uma lista com os nomes dos utilizadores existentes.
     *
     * @return lista com os nomes dos utilizadores
     */
    public List<String> getUtilizadores() throws Exception { // TODO: 29/12/2018 devolver pairs?
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


    /** Array com todos os tipos possíveis de utilizador. */
    public static String[] tiposUtilizador = new String[] {"Administrador", "Repositor", "Vendedor"};


    /**
     * Cria um utilizador no sistema e adiciona-o à base de dados.
     * O tipo de utilizador deve estar contido em {@link #tiposUtilizador}.
     *
     * @param nome      nome do utilizador
     * @param password  password do utilizador
     * @param tipo      tipo do utilizador
     */
    public void criarUtilizador(String nome, String password, String tipo) throws Exception {
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
                    throw new Exception();  // TODO: 29/12/2018 tipo nao existe
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

    // auxiliar
    /**
     * auxiliar
     *
     * @param componentes
     * @return
     */
    private Object[][] componentesListToMatrix(List<Componente> componentes) {
        Object[][] data = new Object[componentes.size()][colunasComponentes.length];
        int i = 0;
        for(Componente c : componentes){
            data[i][0] = c.getCategoria().getDesignacao();
            data[i][1] = c.getId();
            data[i][2] = c.getDesignacao();
            data[i][3] = c.getStock();
            data[i][4] = c.getPreco();
            i++;
        }
        return data;
    }
}
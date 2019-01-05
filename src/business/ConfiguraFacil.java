package business;// import Venda.EncomendaFinalizada;
// import Diagrama_de_packages.Business.EncomendaFinalizada;

import business.gestao.Encomenda;
import business.gestao.EncomendaFinalizada;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class ConfiguraFacil extends Observable {

    private static ConfiguraFacil instancia = new ConfiguraFacil();

	private Utilizador utilizadorAtual;
	private EncomendaAtual encomendaAtual;

	private EncomendaEmProducaoDAO filaProducao = new EncomendaEmProducaoDAO();
	private ComponenteDAO todosComponentes = new ComponenteDAO();
	private PacoteDAO todosPacotes = new PacoteDAO();
	private EncomendaFinalizadaDAO registoProduzidas = new EncomendaFinalizadaDAO();
	private UtilizadorDAO utilizadores = new UtilizadorDAO();


    public static ConfiguraFacil getInstancia() {
        return instancia;
    }

    private ConfiguraFacil(){}


    // -------------------------------- EncomendaFinalizada Atual ----------------------------------------------------------------

    /**
     * Cria uma encomenda com os dados do cliente
     *
     * @param cliente   nome do cliente
     * @param nif       nif do cliente
     * @throws Exception
     */
    public void criarEncomenda(String cliente, int nif) throws FaltamComponenteObrigatorioException, Exception { //muda nome
        int id = new EncomendaFinalizadaDAO().size() + 1;
        for (Categoria categoria : CategoriaManager.getInstance().getAllCategoriasObrigatorias()){
            if(new ComponenteDAO().list(categoria).isEmpty()){
                throw new FaltamComponenteObrigatorioException(categoria.getDesignacao());
            }
        }
        encomendaAtual = new EncomendaAtual(id,cliente, nif);
    }


    public String getDesignacaoComponente (int id) throws Exception {
        try {
            return todosComponentes.get(id).getDesignacao();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    public String getDesignacaoPacote (int id) throws Exception {
        try {
            return todosPacotes.get(id).getDesignacao();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception();
        }
    }

    /**
     * Devolve um Set de ids de componentes que serão removidos.
     * O Set tem os ids dos componentes incompatíveis.
     * @param idComponente   id do componente a adicionar
     *
     * @return  Set de ids de componentes
     */
    public Set<Integer> getIncompatibilidadesComponente(int idComponente) throws ComponenteJaExisteNaConfiguracaoException{
        try {
            Set<Integer> r = encomendaAtual.getIncompatibilidades(idComponente);
            setChanged();
            notifyObservers();
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Devolve um Set de ids de componentes que serão adicionados.
     * O Set tem os ids dos componentes dependentes.
     * @param idComponente   id do componente a adicionar
     *
     * @return  Set de ids de componentes
     */
    public Set<Integer> getDependenciasComponente(int idComponente){
        try {
            Componente c = todosComponentes.get(idComponente);
            return c.getDepedendencias();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Devolve um Set de ids de componentes a remover.
     * O Set tem os ids dos componentes incompatíveis.
     *
     * @param idPacote  id do pacote a adicionar
     * @return
     * @throws PacoteJaExisteNaConfiguracaoException
     * @throws PacoteGeraConflitosException
     */
    public Set<Integer> getIncompatibilidadesPacote(int idPacote) throws PacoteJaExisteNaConfiguracaoException, PacoteGeraConflitosException{
        try {
            Set<Integer> r = this.encomendaAtual.getIncompatibilidadesPacote(idPacote);
            setChanged();
            notifyObservers();
            return r;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Devolve um Set de ids de componentes que serão adicionados.
     * O Set tem os ids dos componentes dependentes de cada componente do pacote.
     * @param idPacote   id do componente a adicionar
     *
     * @return  Set de ids de componentes
     */
    public Set<Integer> getDependenciasPacote(int idPacote){
        HashSet<Integer> res = new HashSet<>();
        try {
            Pacote p = todosPacotes.get(idPacote);
            for(int id : p.getComponentes()){
                Componente c = todosComponentes.get(id);
                res.addAll(c.getDepedendencias());
            }
            return res;
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean criarConfiguracaoOtima(Map<String, Integer> precoMaximoCategoria, int precoMaximoTotal) throws SQLException, FaltamComponenteObrigatorioException { // muda nome
        HashSet<Categoria> categoriasDaConfiguracao = new HashSet<>();
        for(Componente componente : encomendaAtual.getConfiguracao().getComponentes()){
            categoriasDaConfiguracao.add(componente.getCategoria());
        }
        for(Categoria categoria: CategoriaManager.getInstance().getAllCategoriasObrigatorias()){
            if(!categoriasDaConfiguracao.contains(categoria)){
                throw new FaltamComponenteObrigatorioException(categoria.getDesignacao());
            }
        }
        Map<Categoria, Integer> precoMaximoCategoriaNovo = new HashMap<>(precoMaximoCategoria.size());
        for (Map.Entry<String, Integer> entry: precoMaximoCategoria.entrySet()) {
            Categoria categoria = CategoriaManager.getInstance().getCategoria(entry.getKey());
            precoMaximoCategoriaNovo.put(categoria, entry.getValue());
        }
        boolean encontrouSolucaoOtima = encomendaAtual.configuracaoOtima(precoMaximoCategoriaNovo, precoMaximoTotal);
        setChanged();
        notifyObservers();
        return encontrouSolucaoOtima;
    }

    /**
     * blabla
     *
     * @return lista de pacotes formados
     */
    public List<Integer> finalizarEncomenda() throws FaltamDependentesException, FaltamComponenteObrigatorioException, SQLException { // muda nome, devolve pacotes formados
        boolean flag = encomendaAtual.dependentesEmFalta();
        if(flag){
            throw new FaltamDependentesException("");
        }
        flag = encomendaAtual.obrigatoriosEmFalta(new ArrayList<>(CategoriaManager.getInstance().getAllCategoriasObrigatorias()));
        if(flag){
            throw new FaltamComponenteObrigatorioException("");
        }
        //otimos = encomendaAtual.otimizaPacotes()
        try {
            Encomenda feita = encomendaAtual.finalizarEncomenda();
            if(feita instanceof EncomendaFinalizada) {
                registoProduzidas.add((EncomendaFinalizada) feita);
            } else if (feita instanceof EncomendaEmProducao) {
                filaProducao.add((EncomendaEmProducao) feita);
            }
            return new ArrayList<>(); // TODO: 29/12/2018 pacotes formados
        } catch (SQLException e){
            e.printStackTrace();
            throw e; // TODO: 29/12/2018 exceçao fixe
        }
    }


    /**
     * asdsa
     *
     * @return tabela com uma linha por cada categoria obrigatória
     */
    public Object[][] getComponentesObgConfig() { //novo

        Collection<CategoriaObrigatoria> categ = new ArrayList<>();
        categ = CategoriaManager.getInstance().getAllCategoriasObrigatorias();
        List<Componente> comp = encomendaAtual.getComponentesObrigatorios();
        Object[][] data = buildCategObrigatorias(new ArrayList<>(categ));
        for(int i = 0; i<categ.size(); i++)
            for(Componente c : comp) {
                if (c.getCategoria().getDesignacao().equals(data[i][0])) {
                    data[i][1] = c.getId();
                    data[i][2] = c.getDesignacao();
                    data[i][3] = c.getStock();
                    data[i][4] = c.getPreco()/100.0f;
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
            data[i][1] = p.getDesignacao();
            data[i][2] = p.getDesconto()/100.0f;
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
            List<EncomendaFinalizada> encs = registoProduzidas.list();
            Object[][] data = new Object[encs.size()][ConfiguraFacil.colunasRegistoProduzidas.length];
            int i = 0;
            for (EncomendaFinalizada e : encs) {
                data[i][0] = e.getId();
                data[i][1] = e.getCliente();
                data[i][2] = e.getNif();
                data[i][3] = e.getValor()/100.0f;
                List<Integer> comps = new ArrayList<>();
                for (Componente c : e.getComponentes()) {
                    comps.add(c.getId());
                }
                data[i][4] = comps.toString();
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
    public Object[][] getFilaProducao() throws Exception {
        try {
            List<EncomendaEmProducao> encs = filaProducao.list();
            Object[][] data = new Object[encs.size()][ConfiguraFacil.colunasFilaProducao.length];
            int i = 0;
            for (EncomendaEmProducao e : encs) {
                data[i][0] = e.getId();
                data[i][1] = e.getCliente();
                data[i][2] = e.getNif();
                data[i][3] = e.getValor()/100.0f;
                List<Integer> comps = new ArrayList<>();
                for (Componente c : e.getComponentes()) {
                    comps.add(c.getId());
                }
                data[i][4] = comps.toString();
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
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = br.readLine();
        ArrayList<Componente> list = new ArrayList<>();
        while (str != null) {
            String[] data = str.substring(1, str.length() - 1).split("\",\"");
            int id = Integer.parseInt(data[0]);
            String designacao = data[1];
            int preco = Integer.parseInt(data[2]);
            int stock = Integer.parseInt(data[3]);
            String[] dependenciasStrings = data[4].equals("") ? new String[0] : data[4].split(",");
            HashSet<Integer> dependencias = new HashSet<>();
            for(String dependencia:dependenciasStrings){
                dependencias.add(Integer.parseInt(dependencia));
            }
            String[] incompatibilidadesStrings = data[5].equals("") ? new String[0] : data[5].split(",");
            HashSet<Integer> incompatibilidades = new HashSet<>();
            for(String incompatibilidade:incompatibilidadesStrings){
                incompatibilidades.add(Integer.parseInt(incompatibilidade));
            }
            String categoriaDesignacao = data[6];
            Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
            list.add(new Componente(id, designacao, preco, stock, dependencias, incompatibilidades, categoria));
            str = br.readLine();
        }
        new ComponenteDAO().addAll(list);

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
        ArrayList<Pacote> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = br.readLine();
        while (str != null) {
            String[] data = str.substring(1, str.length() - 1).split("\",\"");
            int id = Integer.parseInt(data[0]);
            String designacao = data[1];
            int desconto = Integer.parseInt(data[2]);
            String[] componentesStrings = data[3].equals("") ? new String[0] : data[3].split(",");
            HashSet<Integer> componentes = new HashSet<>();
            for(String componente:componentesStrings){
                componentes.add(Integer.parseInt(componente));
            }
            list.add(new Pacote(id, designacao, desconto, componentes));
            str = br.readLine();
        }

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
        Categoria cat = CategoriaManager.getInstance().getCategoria(categoria);

        try {
            List<Componente> componentes = todosComponentes.list(cat);
            Object[][] componentesTodas = new Object[componentes.size()][colunasComponentesVendedor.length];
            int i = 0;
            for(Componente c : componentes){
                String designacao = c.getDesignacao();
                Categoria cate = c.getCategoria();
                String catDesignacao = cate.getDesignacao();
                float preco = c.getPreco()/100.0f;
                componentesTodas[i] = new Object[]{catDesignacao,designacao,preco};
                i++;
            }
            return componentesTodas;
        } catch (SQLException e) {
            throw new Exception(); // TODO: 29/12/2018 exception fixe
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getComponentes()}. */
    public static String[] colunasComponentesVendedor = new String[] {"Categoria", "Designação", "Preço(€)"};


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
                data[i][2] = p.getDesconto()/100.0f;
                data[i][3] = p.getComponentes().toString();
                i++;
            }
            return data;
        } catch (SQLException e) {
            throw new Exception();
        }
    }

    /**
     * Devolve uma matriz com informações dos pacotes do sistema.
     * Cada linha corresponde a um pacote e as colunas a {@link #colunasPacotes}.
     *
     * @return matriz com uma linha por pacote
     */
    public Object[][] getPacotesVendedor() throws Exception {
        try {
            List<Pacote> pacotes = todosPacotes.list();
            Object[][] data = new Object[pacotes.size()][colunasPacotesVendedor.length];
            int i = 0;
            for(Pacote p : pacotes){
                data[i][0] = p.getDesignacao();
                data[i][1] = p.getDesconto()/100.0f;
                data[i][2] = p.getComponentes().toString();
                i++;
            }
            return data;
        } catch (SQLException e) {
            throw new Exception();
        }
    }

    /** Array com os nomes das colunas da matriz devolvida em {@link #getPacotes()}. */
    public static String[] colunasPacotes = new String[] {"Id", "Designação", "Desconto(€)", "Componentes"};

    /** Array com os nomes das colunas da matriz devolvida em {@link #getPacotesVendedor()}. */
    public static String[] colunasPacotesVendedor = new String[] {"Designação", "Desconto(€)", "Componentes"};

    /**
     * Devolve uma lista de todas as categorias de componentes opcionais.
     *
     * @return lista de categorias
     */
    public List<String> getCategoriasOpcionais() {
        return new ArrayList<>(CategoriaManager.getInstance().getAllCategoriasOpcionaisDesignacao());
    }


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
            data[i][4] = c.getPreco()/100.0f;
            i++;
        }
        return data;
    }
}
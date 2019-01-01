package data;

import business.venda.categorias.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO extends DAO {

    public static void main(String[] args) throws Exception {
        CategoriaDAO cdao = new CategoriaDAO();
        cdao.add(new Carrocaria());
        cdao.add(new Jantes());
        cdao.add(new Motor());
        cdao.add(new Pintura());
        cdao.add(new Pneus());
        cdao.add(new CintoAmarelo());
        cdao.add(new EstofosPelePreto());
        cdao.add(new EstofosPeleVermelha());
        cdao.add(new FaroisXenon());
        cdao.add(new RadioTatil5());
        cdao.add(new RadioTatil7());
        cdao.add(new SensoresLuzChuva());
        cdao.add(new TablierCarbono());
    }

    public boolean add(Categoria categoria) throws SQLException {
        Connection cn = Connect.connect();
        String designacao = categoria.getDesignacao();
        PreparedStatement st = cn.prepareStatement("REPLACE INTO Categoria (designacao) VALUES (?)");
        st.setString(1, designacao);
        int numRows = st.executeUpdate();
        Connect.close(cn);
        return numRows == 1;
    }

    public List<Categoria> list() throws SQLException, CategoriaNaoExisteException {
        Connection cn = Connect.connect();
        ResultSet res = super.getAll(cn, "Categoria");
        List<Categoria> list = new ArrayList<>();
        while (res.next()){
            String designacao = res.getString("designacao");
            list.add(criarCategoria(designacao));
        }
        Connect.close(cn);
        return list;
    }

    public Categoria get(String designacao) throws SQLException, CategoriaNaoExisteException {
        Connection cn = Connect.connect();
        PreparedStatement st = cn.prepareStatement("SELECT 1 FROM Categoria WHERE designacao = ? LIMIT 1");
        st.setString(1, designacao);
        ResultSet res = st.executeQuery();
        if(res.first()) {
            Connect.close(cn);
            return criarCategoria(designacao);
        } else {
            Connect.close(cn);
            return null;
        }
    }

    public boolean remove(String designacao) throws SQLException {
        return super.removeStringKey("Categoria", "designacao", designacao);
    }

    public int size() throws SQLException {
        return super.size("Categoria");
    }
    private Categoria opcionais(String designacao) throws CategoriaNaoExisteException{
        if(designacao == null){
            throw new CategoriaNaoExisteException(null);
        }
        switch (designacao) {
            case "CintoAmarelo":
                return new CintoAmarelo();
            case "EstofosPelePreto":
                return new EstofosPelePreto();
            case "EstofosPeleVermelha":
                return new EstofosPeleVermelha();
            case "FaroisXenon":
                return new FaroisXenon();
            case "RadioTatil5":
                return new RadioTatil5();
            case "RadioTatil7":
                return new RadioTatil7();
            case "SensoresLuzChuva":
                return new SensoresLuzChuva();
            case "TablierCarbono":
                return new TablierCarbono();
            default:
                throw new CategoriaNaoExisteException(null);
        }
    }
    private Categoria criarCategoria(String designacao) throws CategoriaNaoExisteException {
        if(designacao == null){
            throw new CategoriaNaoExisteException(null);
        }
        switch (designacao){
            case "Carrocaria":
                return new Carrocaria();
            case "Jantes":
                return new Jantes();
            case "Motor":
                return new Motor();
            case "Pintura":
                return new Pintura();
            case "Pneus":
                return new Pneus();
            default:
                return opcionais(designacao);
        }
    }

}
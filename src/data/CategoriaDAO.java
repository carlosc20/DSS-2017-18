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
        System.out.println(cdao.add(new Pneus()));
        System.out.println(cdao.list().toString());
        System.out.println(cdao.get("Pneus"));
        System.out.println(cdao.remove("Pneus"));
        System.out.println(cdao.size());
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

    public List<Categoria> list() throws SQLException {
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

    public Categoria get(String designacao) throws SQLException {
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

    private Categoria criarCategoria(String designacao) {
        if(designacao == null){
            return null;
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
                return null;
        }
    }
}
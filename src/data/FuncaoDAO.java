package data;

import business.venda.categorias.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FuncaoDAO extends DAO {

    public boolean add(String funcao) throws SQLException {
        Connection cn = Connect.connect();
        PreparedStatement st = cn.prepareStatement("REPLACE INTO Funcao (designacao) VALUES (?)");
        st.setString(1, funcao);
        int numRows = st.executeUpdate();
        Connect.close(cn);
        return numRows == 1;
    }

    public List<String> list() throws SQLException {
        Connection cn = Connect.connect();
        ResultSet res = super.getAll(cn, "Funcao");
        List<String> list = new ArrayList<>();
        while (res.next()){
            String designacao = res.getString("designacao");
            list.add(designacao);
        }
        Connect.close(cn);
        return list;
    }

    public String get(String funcao) throws SQLException {
        Connection cn = Connect.connect();
        PreparedStatement st = cn.prepareStatement("SELECT 1 FROM Funcao WHERE designacao = ? LIMIT 1");
        st.setString(1, funcao);
        ResultSet res = st.executeQuery();
        if(res.first()) {
            Connect.close(cn);
            return funcao;
        } else {
            Connect.close(cn);
            return null;
        }
    }

    public boolean remove(String funcao) throws SQLException {
        return super.removeStringKey("Funcao", "designacao", funcao);
    }

    public int size() throws SQLException {
        return super.size("Categoria");
    }
}
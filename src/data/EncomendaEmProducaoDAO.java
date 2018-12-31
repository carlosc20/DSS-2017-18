package data;

import business.gestao.EncomendaEmProducao;
import business.gestao.Encomenda;
import business.produtos.Componente;
import business.produtos.Pacote;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class EncomendaEmProducaoDAO extends DAO {

    public boolean add(EncomendaEmProducao encomenda) throws SQLException {
        Connection cn = Connect.connect();
        int id = encomenda.getId();
        String cliente = encomenda.getCliente();
        int nif = encomenda.getNif();
        int valor = encomenda.getValor();
        Date data = Date.valueOf(encomenda.getData());
        Collection<Componente> componentes = encomenda.getComponentes();
        Collection<Pacote> pacotes = encomenda.getPacotes();
        Collection<Componente> componentesEmFalta = encomenda.getComponentesEmFalta();
        try {
            cn.setAutoCommit(false);
            PreparedStatement st = cn.prepareStatement("REPLACE INTO Encomenda (id, cliente, nif, valor, data, finalizada) VALUES (?, ?, ?, ?, ?, 0)");
            st.setInt(1, id);
            st.setString(2, cliente);
            st.setInt(3, nif);
            st.setInt(4, valor);
            st.setDate(5, data);
            int numRows = st.executeUpdate();
            if (numRows != 1) {
                st = cn.prepareStatement("DELETE FROM Encomenda_Componente WHERE id_encomenda = ?");
                st.setInt(1, id);
                st.execute();
                st = cn.prepareStatement("DELETE FROM Encomenda_Pacote WHERE id_encomenda = ?");
                st.setInt(1, id);
                st.execute();
                st = cn.prepareStatement("DELETE FROM Encomenda_Falta WHERE id_encomenda = ?");
                st.setInt(1, id);
                st.execute();
            }
            for (Componente componente : componentes) {
                st = cn.prepareStatement("INSERT INTO Encomenda_Componente (id_encomenda, id_componente, preco) VALUES (?, ?, ?)");
                st.setInt(1, id);
                st.setInt(2, componente.getId());
                st.setInt(3, componente.getPreco());
                st.execute();
            }
            for (Pacote pacote : pacotes) {
                st = cn.prepareStatement("INSERT INTO Encomenda_Pacote (id_encomenda, id_pacote, desconto) VALUES (?, ?, ?)");
                st.setInt(1, id);
                st.setInt(2, pacote.getId());
                st.setInt(3, pacote.getDesconto());
                st.execute();
            }
            for (Componente componente : componentesEmFalta) {
                st = cn.prepareStatement("INSERT INTO Encomenda_Falta (id_encomenda, id_componente) VALUES (?, ?)");
                st.setInt(1, id);
                st.setInt(2, componente.getId());
                st.execute();
            }
            cn.commit();
            return numRows == 1;
        } catch (Exception e) {
            cn.rollback();
            e.printStackTrace();
            throw e;
        } finally {
            Connect.close(cn);
        }
    }

    public List<EncomendaEmProducao> list() throws SQLException {
        Connection cn = Connect.connect();
        PreparedStatement st = cn.prepareStatement("SELECT id, cliente, nif, valor, data FROM Encomenda WHERE finalizada = 0");
        ResultSet res = st.executeQuery();
        List<EncomendaEmProducao> list = new ArrayList<>();
        while (res.next()){
            int id = res.getInt("id");
            String cliente = res.getString("cliente");
            int nif = res.getInt("nif");
            int valor = res.getInt("valor");
            Date data = res.getDate("data");
            list.add(new EncomendaEmProducao(id, cliente, nif, valor, data.toLocalDate(), null, null, null));
        }
        Connect.close(cn);
        return list;
    }

    public EncomendaEmProducao get(int id) throws SQLException {
        Connection cn = Connect.connect();
        PreparedStatement st = cn.prepareStatement("SELECT cliente, nif, valor, data FROM Encomenda WHERE id = ? and finalizada = 0 LIMIT 1");
        st.setString(1, "id");
        ResultSet res = st.executeQuery();
        if(res.first()) {
            String cliente = res.getString("cliente");
            int nif = res.getInt("nif");
            int valor = res.getInt("valor");
            Date data = res.getDate("data");
            Connect.close(cn);
            return new EncomendaEmProducao(id, cliente, nif, valor, data.toLocalDate(), null, null, null);
        } else {
            Connect.close(cn);
            return null;
        }
    }

    public boolean remove(int id) throws SQLException {
        return super.removeIntKey("Encomenda", "id", id);
    }

    public int size() throws SQLException {
        return super.size("Encomenda");
    }
}

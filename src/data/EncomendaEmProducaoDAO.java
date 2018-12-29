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

    public static void main(String[] args) throws SQLException {
        HashSet<Componente> componentes = new HashSet<>();
        componentes.add(new ComponenteDAO().get(1));
        componentes.add(new ComponenteDAO().get(2));
        componentes.add(new ComponenteDAO().get(4));
        componentes.add(new ComponenteDAO().get(5));
        componentes.add(new ComponenteDAO().get(7));
        HashSet<Pacote> pacotes = new HashSet<>();
        pacotes.add(new PacoteDAO().get(3));
        HashSet<Componente> componentesEmFalta = new HashSet<>();
        componentesEmFalta.add(new ComponenteDAO().get(7));
        new EncomendaEmProducaoDAO().add(new EncomendaEmProducao(1, "Cliente 1", 1, 1, LocalDate.now(), componentes, pacotes, componentesEmFalta));
    }

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
        PreparedStatement st = cn.prepareStatement("REPLACE INTO Encomenda (id, cliente, nif, valor, data, finalizada) VALUES (?, ?, ?, ?, ?, 0)");
        st.setInt(1, id);
        st.setString(2, cliente);
        st.setInt(3, nif);
        st.setInt(4, valor);
        st.setDate(5, data);
        int numRows = st.executeUpdate();
        if(numRows != 1) {
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
        for (Componente componente:componentes) {
            st = cn.prepareStatement("INSERT INTO Encomenda_Componente (id_encomenda, id_componente) VALUES (?, ?)");
            st.setInt(1, id);
            st.setInt(2, componente.getId());
            st.execute();
        }
        for (Pacote pacote:pacotes) {
            st = cn.prepareStatement("INSERT INTO Encomenda_Pacote (id_encomenda, id_pacote) VALUES (?, ?)");
            st.setInt(1, id);
            st.setInt(2, pacote.getId());
            st.execute();
        }
        for (Componente componente:componentesEmFalta) {
            st = cn.prepareStatement("INSERT INTO Encomenda_Falta (id_encomenda, id_pacote) VALUES (?, ?)");
            st.setInt(1, id);
            st.setInt(2, componente.getId());
            st.execute();
        }
        Connect.close(cn);
        return numRows == 1;
    }

    public List<EncomendaEmProducao> list() throws SQLException {
        Connection cn = Connect.connect();
        PreparedStatement st = cn.prepareStatement("SELECT cliente, nif, valor, data FROM Encomenda WHERE id = ? and finalizada = 0");
        st.setString(1, "id");
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

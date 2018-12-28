package data;

import business.gestao.Encomenda;
import business.produtos.Componente;
import business.produtos.Pacote;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EncomendaDAO extends DAO {

	public boolean add(Encomenda encomenda) throws SQLException {
		Connection cn = Connect.connect();
		int id = encomenda.getId();
		String cliente = encomenda.getCliente();
		int nif = encomenda.getNif();
		int valor = encomenda.getValor();
		Date data = Date.valueOf(encomenda.getData());
		Collection<Componente> componentes = encomenda.getComponentes();
		Collection<Pacote> pacotes = encomenda.getPacotes();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Encomenda (id, cliente, nif, valor, data, finalizada) VALUES (?, ?, ?, ?, ?, 1)");
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
		Connect.close(cn);
		return numRows == 1;
	}

	public List<Encomenda> list() throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT cliente, nif, valor, data FROM Encomenda WHERE id = ? and finalizada = 1");
		st.setString(1, "id");
		ResultSet res = st.executeQuery();
		List<Encomenda> list = new ArrayList<>();
		while (res.next()){
			int id = res.getInt("id");
			String cliente = res.getString("cliente");
			int nif = res.getInt("nif");
			int valor = res.getInt("valor");
			Date data = res.getDate("data");
			list.add(new Encomenda(id, cliente, nif, valor, data.toLocalDate(), null, null));
		}
		Connect.close(cn);
		return list;
	}

	public Encomenda get(int id) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT cliente, nif, valor, data FROM Encomenda WHERE id = ? and finalizada = 1 LIMIT 1");
		st.setString(1, "id");
		ResultSet res = st.executeQuery();
		if(res.first()) {
			String cliente = res.getString("cliente");
			int nif = res.getInt("nif");
			int valor = res.getInt("valor");
			Date data = res.getDate("data");
			Connect.close(cn);
			return new Encomenda(id, cliente, nif, valor, data.toLocalDate(), null, null);
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

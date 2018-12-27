package data;

import business.produtos.Componente;
import business.produtos.Pacote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PacoteDAO extends DAO {

	public boolean add(Pacote pacote) throws SQLException {
		Connection cn = Connect.connect();
		int id = pacote.getId();
		String designacao = pacote.getDesignacao();
		int desconto = pacote.getDesconto();
		Set<Integer> componentes = pacote.getComponentes();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Pacote (id, designacao, desconto) VALUES (?, ?, ?)");
		st.setInt(1, id);
		st.setString(2, designacao);
		st.setInt(3, desconto);
		int numRows = st.executeUpdate();
		for (int componente:componentes) {
			st = cn.prepareStatement("REPLACE INTO Pacote_Componente(id_pacote, id_componente) VALUES (?, ?)");
			st.setInt(1, id);
			st.setInt(2, componente);
		}
		Connect.close(cn);
		return numRows == 1;
	}

	public List<Pacote> list() throws SQLException {
		Connection cn = Connect.connect();
		ResultSet res = super.getAll(cn, "Pacote");
		List<Pacote> list = new ArrayList<>();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int desconto = res.getInt("desconto");
			list.add(new Pacote(id, designacao, desconto, null));
		}
		Connect.close(cn);
		return list;
	}

	public Pacote get(int id) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT designacao, desconto FROM Pacote WHERE id = ? LIMIT 1");
		st.setInt(1, id);
		ResultSet res = st.executeQuery();
		if(res.first()) {
			String designacao = res.getString("designacao");
			int desconto = res.getInt("desconto");
			Connect.close(cn);
			return new Pacote(id, designacao, desconto, null);
		} else {
			Connect.close(cn);
			return null;
		}
	}

	public void remove(int id) throws SQLException {
		super.removeIntKey("Pacote", "id", id);
	}

	public int size() throws SQLException {
		return super.size("Pacote");
	}

	public Set<Pacote> getPacotesComComponente(int idComponente) throws SQLException {
		Connection cn = Connect.connect();
		Set<Pacote> result = new HashSet<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, desconto FROM Pacote" +
				"INNER JOIN Pacote_Componente ON Pacote.id = Pacote_Componente.id_pacote"
				+ "WHERE Pacote_Componente.id_componente = ?");
		st.setInt(1, idComponente);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int desconto = res.getInt("desconto");
			result.add(new Pacote(id, designacao, desconto, null));
		}
		return result;
	}

	public Set<Componente> getComponentesPacote(int idPacote) throws SQLException {
		Connection cn = Connect.connect();
		Set<Componente> result = new HashSet<>();
		PreparedStatement st = cn.prepareStatement("SELECT id_componente FROM Pacote_Componente WHERE id_pacote = ?");
		st.setInt(1, idPacote);
		ResultSet res = st.executeQuery();
		ComponenteDAO componenteDAO =  new ComponenteDAO();
		while (res.next()){
			int id = res.getInt("id_componente");
			result.add(componenteDAO.get(id));
		}
		return result;
	}

	public Set<Pacote> getPacotesCorrespondentes(Set<Componente> componentes) throws SQLException {
		// Não sei o que é para fazer aqui!!!
		Set<Pacote> result = new HashSet<>();
		for (Componente componente:componentes){
			result.addAll(getPacotesComComponente(componente.getId()));
		}
		return result;
	}
}
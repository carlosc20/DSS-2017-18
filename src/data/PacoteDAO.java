package data;

import business.gestao.EncomendaFinalizada;
import business.produtos.Componente;
import business.produtos.Pacote;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PacoteDAO extends DAO {

	public boolean add(Pacote pacote) throws SQLException {
		Connection cn = Connect.connect();
		int id = pacote.getId();
		String designacao = pacote.getDesignacao();
		int desconto = pacote.getDesconto();
		Set<Integer> componentes = pacote.getComponentes();
		PreparedStatement st = cn.prepareStatement("INSERT INTO Pacote (id, designacao, desconto) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE id = id, designacao = designacao, desconto = desconto");
		st.setInt(1, id);
		st.setString(2, designacao);
		st.setInt(3, desconto);
		int numRows = st.executeUpdate();
		if(numRows != 1) {
			st = cn.prepareStatement("DELETE FROM Pacote_Componente WHERE id_pacote = ?");
			st.setInt(1, id);
			st.execute();
		}
		for (int componente:componentes) {
			st = cn.prepareStatement("INSERT INTO Pacote_Componente (id_pacote, id_componente) VALUES (?, ?)");
			st.setInt(1, id);
			st.setInt(2, componente);
			st.execute();
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
			Set<Integer> componentes = getComponentesId(id);
			list.add(new Pacote(id, designacao, desconto, componentes));
		}
		Connect.close(cn);
		return list;
	}

	public List<Pacote> list(Componente componente) throws SQLException {
		int idComponente = componente.getId();
		Connection cn = Connect.connect();
		List<Pacote> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, desconto FROM Pacote " +
						"INNER JOIN Pacote_Componente ON Pacote.id = Pacote_Componente.id_pacote " +
						"WHERE Pacote_Componente.id_componente = ?");
		st.setInt(1, idComponente);
		System.out.println("oi");
		ResultSet res = st.executeQuery();
		System.out.println("acabei");
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int desconto = res.getInt("desconto");
			Set<Integer> componentes = getComponentesId(id);
			result.add(new Pacote(id, designacao, desconto, componentes));
		}
		return result;
	}

	public List<Pacote> list(EncomendaFinalizada encomenda) throws SQLException {
		int idEncomenda = encomenda.getId();
		Connection cn = Connect.connect();
		List<Pacote> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, Encomenda_Pacote.desconto AS desconto FROM Encomenda_Pacote " +
						"INNER JOIN Pacote ON Pacote.id = Encomenda_Pacote.id_pacote " +
						"WHERE Encomenda_Pacote.id_encomenda = ?");
		st.setInt(1, idEncomenda);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int desconto = res.getInt("desconto");
			Set<Integer> componentes = getComponentesId(id);
			result.add(new Pacote(id, designacao, desconto, componentes));
		}
		return result;
	}

	public Pacote get(int id) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT designacao, desconto FROM Pacote WHERE id = ? LIMIT 1");
		st.setInt(1, id);
		ResultSet res = st.executeQuery();
		if(res.first()) {
			String designacao = res.getString("designacao");
			int desconto = res.getInt("desconto");
			Set<Integer> componentes = getComponentesId(id);
			Connect.close(cn);
			return new Pacote(id, designacao, desconto, componentes);
		} else {
			Connect.close(cn);
			return null;
		}
	}

	public Set<Integer> getComponentesId(int idPacote) throws SQLException {
		Connection cn = Connect.connect();
		Set<Integer> result = new HashSet<>();
		PreparedStatement st = cn.prepareStatement("SELECT id_componente FROM Pacote_Componente WHERE id_pacote = ?");
		st.setInt(1, idPacote);
		ResultSet res = st.executeQuery();
		ComponenteDAO componenteDAO =  new ComponenteDAO();
		while (res.next()){
			int id = res.getInt("id_componente");
			result.add(id);
		}
		return result;
	}

	public void remove(int id) throws SQLException {
		super.removeIntKey("Pacote", "id", id);
	}

	public int size() throws SQLException {
		return super.size("Pacote");
	}

	public void addAll(Collection<Pacote> pacotes) throws SQLException {
		Connection cn = Connect.connect();
		cn.setAutoCommit(false);
		try {
			cn.createStatement().execute("DELETE FROM Pacote_Componente");
			for (Pacote pacote : pacotes){
				PreparedStatement st = cn.prepareStatement("INSERT INTO Pacote (id, designacao, desconto) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE id = id, designacao = designacao, desconto = desconto");
				st.setInt(1, pacote.getId());
				st.setString(2, pacote.getDesignacao());
				st.setInt(3, pacote.getDesconto());
			}
			for (Pacote pacote : pacotes) {
				int pacoteId = pacote.getId();
				for (int componenteId : pacote.getComponentes()) {
					PreparedStatement st = cn.prepareStatement("INSERT INTO Pacote_Componente (id_pacote, id_componente) VALUES (?, ?)");
					st.setInt(1, pacoteId);
					st.setInt(2, componenteId);
					st.execute();
				}
			}
			cn.commit();
		} catch (Exception e){
			cn.rollback();
			throw e;
		} finally {
			Connect.close(cn);
		}
	}
}
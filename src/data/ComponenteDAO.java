package data;

import business.produtos.Componente;
import business.produtos.Pacote;
import business.venda.categorias.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ComponenteDAO extends DAO {

	public boolean add(Componente componente) throws SQLException {
		Connection cn = Connect.connect();
		int id = componente.getId();
		String designacao = componente.getDesignacao();
		int preco = componente.getPreco();
		int stock = componente.getStock();
		Set<Integer> dependencias = componente.getDepedendencias();
		Set<Integer> incompatibilidades = componente.getIncompatibilidades();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Componente (id, designacao, preco, stock) VALUES (?, ?, ?, ?)");
		st.setInt(1, id);
		st.setString(2, designacao);
		st.setInt(3, preco);
		st.setInt(4, stock);
		int numRows = st.executeUpdate();
		for (int dependencia:dependencias) {
			st = cn.prepareStatement("REPLACE INTO Componente_Dependencia (id_componente, id_dependencia) VALUES (?, ?)");
			st.setInt(1, id);
			st.setInt(2, dependencia);
		}
		for (int incompativel:incompatibilidades) {
			st = cn.prepareStatement("REPLACE INTO Componente_Incompatibilidade (id_componente, id_incompativel) VALUES (?, ?)");
			st.setInt(1, id);
			st.setInt(2, incompativel);
		}
		Connect.close(cn);
		return numRows == 1;
	}

	public List<Componente> list() throws SQLException {
		Connection cn = Connect.connect();
		ResultSet res = super.getAll(cn, "Componente", "id, designacao, preco, stock, categoria");
		List<Componente> list = new ArrayList<>();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = new CategoriaDAO().get(categoriaDesignacao);
			list.add(new Componente(id, designacao, preco, stock, null, null, categoria));
		}
		Connect.close(cn);
		return list;
	}

	public List<Componente> list(Pacote pacote) throws SQLException {
		int idPacote = pacote.getId();
		Connection cn = Connect.connect();
		List<Componente> result = new ArrayList<>();
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

	public Componente get(int id) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT designacao, preco, stock, categoria FROM Componente WHERE id = ? LIMIT 1");
		st.setInt(1, id);
		ResultSet res = st.executeQuery();
		if(res.first()) {
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = new CategoriaDAO().get(categoriaDesignacao);
			Connect.close(cn);
			return new Componente(id, designacao, preco, stock, null, null, categoria);
		} else {
			Connect.close(cn);
			return null;
		}
	}

	public boolean remove(int id) throws SQLException {
		return super.removeIntKey("Componente", "id", id);
	}

	public int size() throws SQLException {
		return super.size("Utilizador");
	}

	public Set<Integer> getIncompativeis(int idComponente) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT id_incompativel FROM Componente_Incompativel WHERE id_componente = ?");
		st.setInt(1, idComponente);
		ResultSet res = st.executeQuery();
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		while (res.next()){
			result.add(res.getInt("id_incompativel"));
		}
		Connect.close(cn);
		return result;
	}

	public Set<Integer> getDependentes(int idComponente) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT id_dependente FROM Componente_Dependente WHERE id_componente = ?");
		st.setInt(1, idComponente);
		ResultSet res = st.executeQuery();
		LinkedHashSet<Integer> result = new LinkedHashSet<>();
		while (res.next()){
			result.add(res.getInt("id_dependente"));
		}
		Connect.close(cn);
		return result;
	}

	public Set<Integer> atualizaStock(Set<Componente> componentes) throws SQLException {
		Connection cn = null;
		HashSet<Integer> result = new HashSet<>();
		try {
			for (Componente componente : componentes) {
				int idComponente = componente.getId();
				if (componente.getStock() == 0) {
					result.add(idComponente);
				} else {
					if (cn == null) {
						cn = Connect.connect();
						cn.setAutoCommit(false);
					}
					PreparedStatement st = cn.prepareStatement("UPDATE Componente SET stock = (stock - 1) WHERE id = ?");
					st.setInt(1, idComponente);
					st.execute();
				}
			}
		} catch (SQLException e) {
			cn.rollback();
			throw e;
		} finally {
			cn.commit();
			Connect.close(cn);
		}
		return result;
	}
}
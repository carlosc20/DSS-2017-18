package data;

import business.gestao.EncomendaFinalizadaEmProducao;
import business.gestao.EncomendaFinalizada;
import business.produtos.Componente;
import business.produtos.Pacote;
import business.venda.categorias.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ComponenteDAO extends DAO {

	public void put(int idKey, Componente componente) throws SQLException {
		Connection cn = Connect.connect();
		int id = componente.getId();
		String designacao = componente.getDesignacao();
		int preco = componente.getPreco();
		int stock = componente.getStock();
		String categoria = componente.getCategoria().getDesignacao();
		Set<Integer> dependencias = componente.getDepedendencias();
		Set<Integer> incompatibilidades = componente.getIncompatibilidades();
		PreparedStatement st = cn.prepareStatement("INSERT INTO Componente (id, designacao, preco, stock, categoria) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id = id, designacao = designacao, preco = preco, stock = stock, categoria = categoria");
		st.setInt(1, id);
		st.setString(2, designacao);
		st.setInt(3, preco);
		st.setInt(4, stock);
		st.setString(5, categoria);
		int numRows = st.executeUpdate();
		if(numRows != 1) {
			st = cn.prepareStatement("DELETE FROM Componente_Dependente WHERE id_componente = ?");
			st.setInt(1, id);
			st.execute();
			st = cn.prepareStatement("DELETE FROM Componente_Incompativel WHERE id_componente = ?");
			st.setInt(1, id);
			st.execute();
		}
		for (int dependencia:dependencias) {
			st = cn.prepareStatement("INSERT INTO Componente_Dependente (id_componente, id_dependente) VALUES (?, ?)");
			st.setInt(1, id);
			st.setInt(2, dependencia);
			st.execute();
		}
		for (int incompativel:incompatibilidades) {
			st = cn.prepareStatement("INSERT INTO Componente_Incompativel (id_componente, id_incompativel) VALUES (?, ?)");
			st.setInt(1, id);
			st.setInt(2, incompativel);
			st.execute();
		}
		Connect.close(cn);
	}

	public List<Componente> list() throws SQLException {
		Connection cn = Connect.connect();
		ResultSet res = super.getAll(cn, "Componente");
		List<Componente> list = new ArrayList<>();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
			list.add(new Componente(id, designacao, preco, stock, dep, inc, categoria));
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

	public List<Componente> list(Categoria categoria) throws SQLException {
		String designacaoCategoria = categoria.getDesignacao();
		Connection cn = Connect.connect();
		List<Componente> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement("SELECT id, designacao, preco, stock FROM Componente WHERE categoria = ?");
		st.setString(1, designacaoCategoria);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			result.add(new Componente(id, designacao, preco, stock, dep, inc, categoria));
		}
		return result;
	}

	public List<Componente> list(EncomendaFinalizada encomenda) throws SQLException {
		int idEncomenda = encomenda.getId();
		Connection cn = Connect.connect();
		List<Componente> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, Encomenda_Componente.preco AS preco, stock, categoria " +
						"FROM Encomenda_Componente " +
						"INNER JOIN Componente ON Encomenda_Componente.id_componente = Componente.id " +
						"WHERE Encomenda_Componente.id_encomenda = ?");
		st.setInt(1, idEncomenda);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, dep, inc, categoria));
		}
		return result;
	}

	public List<Componente> listComponentesDependentes(Componente componente) throws SQLException {
		int idComponente = componente.getId();
		Connection cn = Connect.connect();
		List<Componente> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, preco, stock, categoria " +
						"FROM Componente " +
						"INNER JOIN Componente_Dependente ON Componente_Dependente.id_dependente = Componente.id " +
						"WHERE Componente_Dependente.id_componente = ?");
		st.setInt(1, idComponente);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, dep, inc, categoria));
		}
		return result;
	}

	public List<Componente> listComponentesIncompativeis(Componente componente) throws SQLException {
		int idComponente = componente.getId();
		Connection cn = Connect.connect();
		List<Componente> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, preco, stock, categoria " +
						"FROM Componente " +
						"INNER JOIN Componente_Incompativel ON Componente_Incompativel.id_incompativel = Componente.id " +
						"WHERE Componente_Incompativel.id_componente = ?");
		st.setInt(1, idComponente);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, dep, inc, categoria));
		}
		return result;
	}

	public List<Componente> listComponentesEmFalta(EncomendaFinalizadaEmProducao encomenda) throws SQLException {
		int idEncomenda = encomenda.getId();
		Connection cn = Connect.connect();
		List<Componente> result = new ArrayList<>();
		PreparedStatement st = cn.prepareStatement(
				"SELECT id, designacao, Encomenda_Componente.preco AS preco, stock, categoria " +
						"FROM Encomenda_Componente " +
						"INNER JOIN Componente ON Encomenda_Componente.id_componente = Componente.id " +
						"INNER JOIN Encomenda_Falta ON Encomenda_Falta.id_componente = Componente.id " +
						"WHERE Encomenda_Falta.id_encomenda = ?");
		st.setInt(1, idEncomenda);
		ResultSet res = st.executeQuery();
		while (res.next()){
			int id = res.getInt("id");
			String designacao = res.getString("designacao");
			int preco = res.getInt("preco");
			int stock = res.getInt("stock");
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, dep, inc, categoria));
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
			Set<Integer> dep = getDependentes(id);
			Set<Integer> inc = getIncompativeis(id);
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = CategoriaManager.getInstance().getCategoria(categoriaDesignacao);
			Connect.close(cn);
			return new Componente(id, designacao, preco, stock, dep, inc, categoria);
		} else {
			Connect.close(cn);
			return null;
		}
	}

	public boolean remove(int id) throws SQLException {
		return super.removeIntKey("Componente", "id", id);
	}

	public int size() throws SQLException {
		return super.size("Componente");
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

	public Set<Componente> atualizaStock(List<Componente> componentes) throws SQLException {
		Connection cn = null;
		HashSet<Componente> result = new HashSet<>();
		try {
			for (Componente componente : componentes) {
				if (componente.getStock() == 0) {
					result.add(componente);
				} else {
					if (cn == null) {
						cn = Connect.connect();
						cn.setAutoCommit(false);
					}
					PreparedStatement st = cn.prepareStatement("UPDATE Componente SET stock = (stock - 1) WHERE id = ?");
					st.setInt(1, componente.getId());
					st.execute();
				}
			}
			if(cn != null) {
				cn.commit();
			}
		} catch (SQLException e) {
			cn.rollback();
			throw e;
		} finally {
			Connect.close(cn);
		}
		return result;
	}

	public void addAll(Collection<Componente> componentes) throws SQLException {
		Connection cn = Connect.connect();
		cn.setAutoCommit(false);
		try {
			cn.createStatement().execute("DELETE FROM Componente_Dependente");
			cn.createStatement().execute("DELETE FROM Componente_Incompativel");
			for (Componente componente : componentes){
				PreparedStatement st = cn.prepareStatement("INSERT INTO Componente (id, designacao, preco, stock, categoria) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE id = ?, designacao = ?, preco = ?, stock = ?, categoria = ?");
				st.setInt(1, componente.getId());
				st.setString(2, componente.getDesignacao());
				st.setInt(3, componente.getPreco());
				st.setInt(4, componente.getStock());
				st.setString(5, componente.getCategoria().getDesignacao());
				st.setInt(6, componente.getId());
				st.setString(7, componente.getDesignacao());
				st.setInt(8, componente.getPreco());
				st.setInt(9, componente.getStock());
				st.setString(10, componente.getCategoria().getDesignacao());
				st.execute();
			}
			for (Componente componente : componentes){
				int componenteId = componente.getId();
				for (int dependencia : componente.getDepedendencias()) {
					PreparedStatement st = cn.prepareStatement("INSERT INTO Componente_Dependente (id_componente, id_dependente) VALUES (?, ?)");
					st.setInt(1, componenteId);
					st.setInt(2, dependencia);
					st.execute();
				}
				for (int incompatibilidade : componente.getIncompatibilidades()) {
					PreparedStatement st = cn.prepareStatement("INSERT INTO Componente_Incompativel (id_componente, id_incompativel) VALUES (?, ?)");
					st.setInt(1, componenteId);
					st.setInt(2, incompatibilidade);
					st.execute();
				}
			}
			cn.commit();
		} catch (Exception e){
			cn.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			Connect.close(cn);
		}
	}
}
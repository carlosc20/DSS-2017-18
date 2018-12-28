package data;

import business.gestao.EncomendaEmProducao;
import business.gestao.Encomenda;
import business.produtos.Componente;
import business.produtos.Pacote;
import business.venda.categorias.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ComponenteDAO extends DAO {

	public static void main(String[] args) throws Exception {
		ComponenteDAO cdao = new ComponenteDAO();
		HashSet<Integer> depPneus1 = new HashSet<>();
		depPneus1.add(2);
		HashSet<Integer> incPneus1 = new HashSet<>();
		incPneus1.add(3);
		HashSet<Integer> depJantes1 = new HashSet<>();
		HashSet<Integer> incJantes1 = new HashSet<>();
		HashSet<Integer> depJantes2 = new HashSet<>();
		HashSet<Integer> incJantes2 = new HashSet<>();
		incJantes2.add(1);
		HashSet<Integer> depCarrocaria1 = new HashSet<>();
		HashSet<Integer> incCarrocaria1 = new HashSet<>();
		incCarrocaria1.add(3);
		HashSet<Integer> depMotor1 = new HashSet<>();
		depMotor1.add(4);
		HashSet<Integer> incMotor1 = new HashSet<>();
		HashSet<Integer> depPintura1 = new HashSet<>();
		HashSet<Integer> incPintura1 = new HashSet<>();
		incPintura1.add(4);
		HashSet<Integer> depPintura2 = new HashSet<>();
		depPintura2.add(4);
		HashSet<Integer> incPintura2 = new HashSet<>();
		cdao.add(new Componente(2, "Jantes 1", 10000, 3, depJantes1, incJantes1, new Jantes()));
		cdao.add(new Componente(3, "Jantes 2", 15000, 20, depJantes2, incJantes2, new Jantes()));
		cdao.add(new Componente(1, "Pneus 1", 20000, 5, depPneus1, depPneus1, new Pneus()));
		cdao.add(new Componente(4, "Carrocaria 1", 50000, 5, depCarrocaria1, incCarrocaria1, new Carrocaria()));
		cdao.add(new Componente(5, "Motor 1", 30000, 4, depMotor1, incMotor1, new Motor()));
		cdao.add(new Componente(6, "Pitura 1", 10000, 99, depPintura1, incPintura1, new Pintura()));
		cdao.add(new Componente(7, "Pintura 2", 20000, 0, depPintura2, incPintura2, new Pintura()));
	}

	public boolean add(Componente componente) throws SQLException {
		Connection cn = Connect.connect();
		int id = componente.getId();
		String designacao = componente.getDesignacao();
		int preco = componente.getPreco();
		int stock = componente.getStock();
		String categoria = componente.getCategoria().getDesignacao();
		Set<Integer> dependencias = componente.getDepedendencias();
		Set<Integer> incompatibilidades = componente.getIncompatibilidades();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Componente (id, designacao, preco, stock, categoria) VALUES (?, ?, ?, ?, ?)");
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
		return numRows == 1;
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
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = criarCategoria(categoriaDesignacao);
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
			result.add(new Componente(id, designacao, preco, stock, null, null, categoria));
		}
		return result;
	}

	public List<Componente> list(Encomenda encomenda) throws SQLException {
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
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = criarCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, null, null, categoria));
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
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = criarCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, null, null, categoria));
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
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = criarCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, null, null, categoria));
		}
		return result;
	}

	public List<Componente> listComponentesEmFalta(EncomendaEmProducao encomenda) throws SQLException {
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
			String categoriaDesignacao = res.getString("categoria");
			Categoria categoria = criarCategoria(categoriaDesignacao);
			result.add(new Componente(id, designacao, preco, stock, null, null, categoria));
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
			Categoria categoria = criarCategoria(categoriaDesignacao);
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

	public Set<Componente> atualizaStock(Set<Componente> componentes) throws SQLException {
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
		} catch (SQLException e) {
			cn.rollback();
			throw e;
		} finally {
			cn.commit();
			Connect.close(cn);
		}
		return result;
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
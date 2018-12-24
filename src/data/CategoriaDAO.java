package data;

import business.venda.categorias.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO extends DAO {

	public boolean add(Categoria categoria) throws SQLException {
		Connection cn = Connect.connect();
		String designacao = categoria.getDesignacao();
		String subcategoria = categoria.getSubcategoria();
		boolean obrigatoria = categoria.getObrigatoria();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Utilizador (nome, password, funcao) VALUES (?, ?, ?)");
		st.setString(1, designacao);
		st.setString(2, subcategoria);
		st.setBoolean(3, obrigatoria);
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
			String subcategoria = res.getString("subcategoria");
			boolean obrigatoria = res.getBoolean("obrigatoria");
			list.add(criarCategoria(designacao, subcategoria, obrigatoria));
		}
		Connect.close(cn);
		return list;
	}

	public Categoria get(String designacao) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT password, funcao FROM Utilizador LIMIT 1 WHERE nome = ?");
		st.setString(1, designacao);
		ResultSet res = st.executeQuery();
		Connect.close(cn);
		if(res.first()) {
			String subcategoria = res.getString("subcategoria");
			boolean obrigatoria = res.getBoolean("obrigatoria");
			Connect.close(cn);
			return criarCategoria(designacao, subcategoria, obrigatoria);
		} else {
			return null;
		}
	}

	public boolean remove(Categoria categoria) throws SQLException {
		return super.remove("Categoria", "designacao", categoria.getDesignacao());
	}

	public int size() throws SQLException {
		return super.size("Categoria");
	}

	private Categoria criarCategoria(String designacao, String subcategoria, boolean obrigatoria) {
		if(obrigatoria) {
			switch (subcategoria){
				case "Carrocaria":
					return new Carrocaria(designacao);
				case "Jantes":
					return new Jantes(designacao);
				case "Motor":
					return new Motor(designacao);
				case "Pintura":
					return new Pintura(designacao);
				case "Pneus":
					return new Pneus(designacao);
				default:
					return new CategoriaObrigatoria(designacao);
			}
		} else {
			return new CategoriaOpcional(designacao);
		}
	}
}
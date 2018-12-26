package data;

import business.venda.categorias.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO extends DAO {

	public static void main(String[] args) throws Exception {
		CategoriaDAO cdao = new CategoriaDAO();
		System.out.println(cdao.add(new Pneus("Michelin Primacy 4")));
		System.out.println(cdao.list().toString());
		System.out.println(cdao.get("Michelin Primacy 4").getSubcategoria());
		System.out.println(cdao.remove("Michelin Primacy 4"));
		System.out.println(cdao.size());
	}

	public boolean add(Categoria categoria) throws SQLException {
		Connection cn = Connect.connect();
		String designacao = categoria.getDesignacao();
		String subcategoria = categoria.getSubcategoria();
		boolean obrigatoria = categoria.getObrigatoria();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Categoria (designacao, subcategoria, obrigatoria) VALUES (?, ?, ?)");
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

	public Categoria get(String designacao) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT subcategoria, obrigatoria FROM Categoria WHERE designacao = ? LIMIT 1");
		st.setString(1, designacao);
		ResultSet res = st.executeQuery();
		if(res.first()) {
			String subcategoria = res.getString("subcategoria");
			boolean obrigatoria = res.getBoolean("obrigatoria");
			Connect.close(cn);
			return criarCategoria(designacao, subcategoria, obrigatoria);
		} else {
			return null;
		}
	}

	public boolean remove(String designacao) throws SQLException {
		return super.remove("Categoria", "designacao", designacao);
	}

	public int size() throws SQLException {
		return super.size("Categoria");
	}

	private Categoria criarCategoria(String designacao, String subcategoria, boolean obrigatoria) {
		if(obrigatoria) {
			if(subcategoria == null){
				return new CategoriaObrigatoria(designacao);
			}
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
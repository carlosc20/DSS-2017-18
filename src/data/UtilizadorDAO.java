package data;

import business.utilizadores.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtilizadorDAO extends DAO {

	public boolean add(Utilizador utilizador) throws SQLException {
		Connection cn = Connect.connect();
		String nome = utilizador.getNome();
		String password = utilizador.getPassword();
		String funcao = utilizador.getFuncao();
		PreparedStatement st = cn.prepareStatement("REPLACE INTO Utilizador (nome, password, funcao) VALUES (?, ?, ?)");
		st.setString(1, nome);
		st.setString(2, password);
		st.setString(3, funcao);
		int numRows = st.executeUpdate();
		Connect.close(cn);
		return numRows == 1;
	}

	public List<Utilizador> list() throws SQLException, FuncaoNaoExisteExcpetion {
		Connection cn = Connect.connect();
		ResultSet res = super.getAll(cn, "Utilizador");
		List<Utilizador> list = new ArrayList<>();
		while (res.next()){
			String nome = res.getString("nome");
			String password = res.getString("password");
			String funcao = res.getString("funcao");
			list.add(criarUtilizador(nome, password, funcao));
		}
		Connect.close(cn);
		return list;
	}

	public Utilizador get(String nome) throws SQLException, FuncaoNaoExisteExcpetion {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("SELECT password, funcao FROM Utilizador WHERE nome = ? LIMIT 1");
		st.setString(1, nome);
		ResultSet res = st.executeQuery();
		if(res.first()) {
			String password = res.getString("password");
			String funcao = res.getString("funcao");
			Connect.close(cn);
			System.out.println(password);
			return criarUtilizador(nome, password, funcao);
		} else {
			System.out.println("ola");
			Connect.close(cn);
			return null;
		}
	}

	public boolean remove(String nome) throws SQLException {
		return super.removeStringKey("Utilizador", "nome", nome);
	}

	public int size() throws SQLException {
		return super.size("Utilizador");
	}

	private Utilizador criarUtilizador(String nome, String password, String funcao) throws FuncaoNaoExisteExcpetion {
		if(funcao == null){
			throw new FuncaoNaoExisteExcpetion(null);
		}
		switch (funcao){
			case "Vendedor":
				return new Vendedor(nome, password);
			case "Administrador":
				return new Administrador(nome, password);
			case "Repositor":
				return new Repositor(nome, password);
			default:
				throw new FuncaoNaoExisteExcpetion(funcao) ;
		}
	}
}
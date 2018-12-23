package data;

import business.utilizadores.Administrador;
import business.utilizadores.Repositor;
import business.utilizadores.Utilizador;
import business.utilizadores.Vendedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtilizadorDAO extends DAO {

	public UtilizadorDAO(String url, String user, String password) {
		super(url, user, password);
	}

	public void put(String id, Utilizador utilizador) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect(url, user, password);
		String nome = utilizador.getNome();
		String password = utilizador.getPassword();
		String funcao = utilizador.getClass().getSimpleName();

		PreparedStatement st = cn.prepareStatement("REPLACE INTO Utilizador (nome, password, funcao) VALUES (?, ?, ?)");
		st.setString(1, nome);
		st.setString(2, password);
		st.setString(3, funcao);
		st.execute();
		Connect.close(cn);
	}

	public List<Utilizador> list(String condition) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect(url, user, password);
		ResultSet res = super.get("utilizador", condition);
		List<Utilizador> list = new ArrayList<>();
		while (res.next()){
			String nome = res.getString("nome");
			String password = res.getString("password");
			String funcao = res.getString("funcao");
			list.add(createUtilizador(nome, password, funcao));
		}
		Connect.close(cn);
		return list;
	}

	public Utilizador get(String nome) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect(url, user, password);
		PreparedStatement st = cn.prepareStatement("SELECT password, funcao FROM Utilizador LIMIT 1 WHERE nome = ?");
		st.setString(1, nome);
		ResultSet res = st.executeQuery();
		Connect.close(cn);
		res.first();
		String password = res.getString("password");
		String funcao = res.getString("funcao");
		return createUtilizador(nome, password, funcao);
	}

	public void delete(Utilizador utilizador)  throws ClassNotFoundException, SQLException {
		super.remove("Utilizador", "nome", utilizador.getNome());
	}

	public int size()  throws ClassNotFoundException, SQLException {
		super.size("Utilizador");
	}

	private Utilizador createUtilizador(String nome, String password, String funcao) {
		switch (funcao){
			case "Vendedor":
				return new Vendedor(nome, password);
			case "Administrador":
				return new Administrador(nome, password);
			case "Repositor":
				return new Repositor(nome, password);
			default:
				return new Utilizador(nome, password);
		}
	}
}
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DAO {

	protected String url;
	protected String user;
	protected String password;

	public DAO () {}
	public DAO (String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void remove(String table, String column, String key) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect(url,user, password);
		PreparedStatement st = cn.prepareStatement("DELETE FROM ? WHERE ? = ?");
		st.setString(1, table);
		st.setString(2, column);
		st.setString(3, key);
		st.execute();
		Connect.close(cn);
	}

	public int size(String table) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect(url,user, password);
		PreparedStatement st = cn.prepareStatement("SELECT COUNT(*) FROM ?");
		st.setString(1, table);
		ResultSet res = st.executeQuery();
		Connect.close(cn);
		if(res.first()){
			return res.getInt(1);
		} else {
			return 0;
		}
	}

	public ResultSet get(String table, String condition) throws ClassNotFoundException, SQLException {
		Connection cn = Connect.connect(url,user, password);
		PreparedStatement st = cn.prepareStatement("SELECT * FROM ? WHERE " + condition);
		st.setString(1, table);
		ResultSet res = st.executeQuery();
		Connect.close(cn);
		return res;
	}
}
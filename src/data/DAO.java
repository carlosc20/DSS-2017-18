package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAO {

	protected boolean remove(String table, String column, String key) throws SQLException {
		Connection cn = Connect.connect();
		PreparedStatement st = cn.prepareStatement("DELETE FROM " + table + " WHERE " + column + " = ?");
		st.setString(1, key);
		int numRows = st.executeUpdate();
		Connect.close(cn);
		return numRows == 1;
	}

	protected int size(String table) throws SQLException {
		Connection cn = Connect.connect();
		ResultSet res = cn.createStatement().executeQuery("SELECT COUNT(*) FROM " + table);
		int numRows;
		if(res.first()){
			numRows = res.getInt(1);
		} else {
			numRows = 0;
		}
		Connect.close(cn);
		return numRows;
	}

	protected ResultSet getAll(Connection cn, String table) throws SQLException {
		return cn.createStatement().executeQuery("SELECT * FROM " + table);
	}
}
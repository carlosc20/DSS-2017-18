package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    static public Connection connect(String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }

    static public void close (Connection con) throws SQLException {
        con.close();
    }
}
package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    private static final String URL = "sql7.freemysqlhosting.net";
    private static final String SCHEMA = "sql7271350";
    private static final String USERNAME = "sql7271350";
    private static final String PASSWORD = "h4zuDkhHCa";

    public static Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + URL + "/" + SCHEMA, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean close (Connection con) {
        try {
            con.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
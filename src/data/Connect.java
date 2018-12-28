package data;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {

    private static final String URL = "db4free.net:3306";
    private static final String SCHEMA = "configfacil";
    private static final String USERNAME = "configfacil";
    private static final String PASSWORD = "configfacil";

    public static Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://" + URL + "/" + SCHEMA + "?autoReconnect=true&useSSL=false", USERNAME, PASSWORD);
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
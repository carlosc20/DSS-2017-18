package data;

import java.sql.Connection;
import java.sql.DriverManager;

public class Connect {

    private static final String URL = "db4free.net:3306";
    private static final String SCHEMA = "configfacil";
    private static final String USERNAME = "configfacil";
    private static final String PASSWORD = "configfacil";

    static Connection connection = null;

    public static Connection connect() {
        try {
            if(connection == null || connection.isClosed()){
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("A ligar à base de dados");
                connection = DriverManager.getConnection("jdbc:mysql://" + URL + "/" + SCHEMA + "?useSSL=false", USERNAME, PASSWORD);
                System.out.println("Ligação à base de dados concluida");
            }
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean close (Connection con) {
        try {
            //con.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
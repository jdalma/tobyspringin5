package springbook.chapter01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DUserDao extends UserDao{

    private final String DRIVER = "com.mysql.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost/springbook?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private final String ID = "springbook";
    private final String PASSWORD = "springbook!@";

    @Override
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL,ID,PASSWORD);
    }
}
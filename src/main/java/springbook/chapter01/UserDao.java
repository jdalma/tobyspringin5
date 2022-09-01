package springbook.chapter01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private final String DRIVER = "com.mysql.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost/springbook?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private final String ID = "springbook";
    private final String PASSWORD = "springbook!@";

    public void add(User user) throws ClassNotFoundException , SQLException{
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement("insert into users(id , name , password) values(? , ? , ?)");
        ps.setString(1 , user.getId());
        ps.setString(2 , user.getName());
        ps.setString(3 , user.getPassword());

        ps.executeUpdate();

        ps.close();
        con.close();
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection con = getConnection();
        PreparedStatement ps = con.prepareStatement("select * from users where id = ?");
        ps.setString(1 , id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        ps.close();
        con.close();
        return user;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(DRIVER);
        return DriverManager.getConnection(URL,ID,PASSWORD);
    }
}

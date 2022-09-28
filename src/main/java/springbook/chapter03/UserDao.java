package springbook.chapter03;

import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws SQLException{
        StatementStrategy st = new AddStatement(user);
        jdbcContextWithStatementStrategy(st);
    }

    public User get(String id) throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("select * from users where id = ?");
        ps.setString(1 , id);

        ResultSet rs = ps.executeQuery();
        User user = null;
        if(rs.next()){
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        ps.close();
        con.close();

        if(user == null){
            throw new EmptyResultDataAccessException(1);
        }

        return user;
    }

    public int deleteAll() throws SQLException{
        return jdbcContextWithStatementStrategy(new DeleteAllStatement());
    }

    public int jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        int count = 0;
        try {
            con = dataSource.getConnection();
            ps = stmt.makePreparedStatement(con);
            ps.executeUpdate();
        } catch (SQLException e){
            throw e;
        } finally {
            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            if(con != null){
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
        ps.close();
        con.close();

        return count;
    }

    public int getCount() throws SQLException {
        Connection con = dataSource.getConnection();
        PreparedStatement ps = con.prepareStatement("select count(*) from users");

        ResultSet rs = ps.executeQuery();
        rs.next();
        int count = rs.getInt(1);

        rs.close();
        ps.close();
        con.close();

        return count;
    }
}

package springbook.chapter03;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcContext {
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int workWithStatementStrategy(StatementStrategy stmt) throws SQLException {
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
}

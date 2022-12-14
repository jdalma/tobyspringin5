package springbook.chapter03;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class TestDBConfig {

    private final String URL = "jdbc:mysql://localhost/springbook?characterEncoding=UTF-8";
    private final String ID = "spring";
    private final String PASSWORD = "book";

    @Bean
    public UserDao userDao(){
        UserDao dao = new UserDao();
        dao.setDataSource(dataSource());
        return dao;
    }

    @Bean
    public JdbcContext jdbcContext() {
        JdbcContext jdbcContext = new JdbcContext();
        jdbcContext.setDataSource(dataSource());

        return jdbcContext;
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
        dataSource.setUrl(URL);
        dataSource.setUsername(ID);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }
}

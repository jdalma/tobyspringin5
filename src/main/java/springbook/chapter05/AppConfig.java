package springbook.chapter05;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    private final String URL = "jdbc:mysql://localhost/springbook?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private final String ID = "spring";
    private final String PASSWORD = "book";

    @Bean
    public UserService userService() {
        UserService userService = new UserService();
        userService.setUserDao(userDao());
        userService.setUserLevelUpgradePolicy(userLevelService());
        userService.setDataSource(dataSource());
        return userService;
    }

    @Bean
    public UserLevelUpgradePolicy userLevelService() {
        UserLevelService userLevelService = new UserLevelService();
        userLevelService.setUserDao(userDao());
        return userLevelService;
    }

    @Bean
    public UserDaoJdbc userDao(){
        UserDaoJdbc dao = new UserDaoJdbc();
        dao.setDataSource(dataSource());
        return dao;
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl(URL);
        dataSource.setUsername(ID);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }
}

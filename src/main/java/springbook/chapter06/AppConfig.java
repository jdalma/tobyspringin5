package springbook.chapter06;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class AppConfig {

    private final String URL = "jdbc:mysql://localhost/springbook?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private final String ID = "spring";
    private final String PASSWORD = "book";

    @Bean
    public UserService userService() {
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSenderImpl());
        return userService;
    }

    @Bean
    public UserService userServiceTx() {
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setUserService(userServiceTx);
        userServiceTx.setTransactionManager(transactionManager());
        return userServiceTx;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public MailSender mailSenderImpl() {
        return new DummyMailSender();
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

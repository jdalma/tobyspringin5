package springbook.chapter07;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springbook.chapter06.DummyMailSender;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "springbook.chapter07")
@Import(SqlServiceContext.class)
public class AppContext {

    private final String URL = "jdbc:mysql://localhost/springbook?characterEncoding=UTF-8";
    private final String ID = "spring";
    private final String PASSWORD = "book";

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
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

    @Configuration
    @Profile("test")
    public static class TestAppContext {

        @Autowired
        private UserDao userDao;

        @Bean
        public UserService userOnlyTestServiceImpl() {
            TestUserService testService = new TestUserService();
            testService.setUserDao(this.userDao);
            testService.setMailSender(mailSenderImpl());
            return testService;
        }

        @Bean
        public MailSender mailSenderImpl() {
            return new DummyMailSender();
        }
    }

    @Configuration
    @Profile("production")
    public static class ProductionAppContext {
        @Bean
        public MailSender mailSenderImpl() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("mail.test.com");
            return mailSender;
        }
    }

}

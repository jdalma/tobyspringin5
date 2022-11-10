package springbook.chapter07;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springbook.chapter06.DummyMailSender;
import springbook.chapter07.sqlService.EnableSqlService;
import springbook.chapter07.sqlService.SqlMapConfig;
import springbook.chapter07.sqlService.UserSqlMapConfig;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@EnableTransactionManagement
@EnableSqlService
@ComponentScan(basePackages = "springbook.chapter07")
@PropertySource("/database.properties")
public class AppContext {

    @Value("${db.driverClass}") Class<? extends Driver> driverClass;
    @Value("${db.url}") String url;
    @Value("${db.username}") String username;
    @Value("${db.password}") String password;

    @Bean
    public SqlMapConfig sqlMapConfig() {
        return new UserSqlMapConfig();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(this.driverClass);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);

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

package springbook.chapter07;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import springbook.chapter06.DummyMailSender;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class TestDBConfig {

    private final String URL = "jdbc:mysql://localhost/springbook?characterEncoding=UTF-8";
    private final String ID = "spring";
    private final String PASSWORD = "book";

    @Bean
    public UserService userServiceImpl() {
        UserServiceImpl userService = new UserServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSenderImpl());
        return userService;
    }

    @Bean
    public UserService userOnlyTestServiceImpl() {
        UserOnlyTestServiceImpl userService = new UserOnlyTestServiceImpl();
        userService.setUserDao(userDao());
        userService.setMailSender(mailSenderImpl());
        return userService;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public TransactionInterceptor transactionAdvice() {
        Properties properties = new Properties();
        properties.setProperty("get*", "PROPAGATION_REQUIRED,readOnly,timeout_30");
        properties.setProperty("upgrade*", "PROPAGATION_REQUIRES_NEW, ISOLATION_SERIALIZABLE");
        properties.setProperty("*", "PROPAGATION_REQUIRED");

        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(transactionManager());
        interceptor.setTransactionAttributes(properties);
        return interceptor;
    }

    @Bean
    public AspectJExpressionPointcut transactionPointcut() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* *..*Service.*(..))");
        return pointcut;
    }
    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        DefaultPointcutAdvisor defaultPointcutAdvisor = new DefaultPointcutAdvisor();
        defaultPointcutAdvisor.setAdvice(transactionAdvice());
        defaultPointcutAdvisor.setPointcut(transactionPointcut());
        return defaultPointcutAdvisor;
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
        dao.setSqlService(sqlService());
        return dao;
    }

    @Bean
    public SqlService sqlService() {
        return new XmlSqlService();
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

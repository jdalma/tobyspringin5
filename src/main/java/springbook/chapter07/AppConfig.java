package springbook.chapter07;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import springbook.chapter06.DummyMailSender;
import springbook.chapter06.factoryBean.MessageFactoryBean;
import springbook.chapter07.sqlService.ConcurrentHashMapSqlRegistry;
import springbook.chapter07.sqlService.HashMapSqlRegistry;
import springbook.chapter07.sqlService.JaxbXmlSqlReader;
import springbook.chapter07.sqlService.OxmSqlService;
import springbook.chapter07.sqlService.SqlReader;
import springbook.chapter07.sqlService.SqlRegistry;

import javax.sql.DataSource;
import java.util.Properties;

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
    public TransactionInterceptor transactionAdvice() {
        Properties properties = new Properties();
        properties.setProperty("get*", "PROPAGATION_REQUIRED,readOnly,timeout_30");
        properties.setProperty("upgrade*", "PROPAGATION_REQUIRED_NEW, ISOLATION_SERIALIZABLE");
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
    public MessageFactoryBean messageFactoryBean() {
        MessageFactoryBean messageFactoryBean = new MessageFactoryBean();
        messageFactoryBean.setText("Factory Bean");
        return messageFactoryBean;
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
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }

    @Bean
    public SqlReader jaxbXmlSqlReader() {
        JaxbXmlSqlReader jaxbXmlSqlReader = new JaxbXmlSqlReader();
        return jaxbXmlSqlReader;
    }

    @Bean
    public Unmarshaller unmarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath("springbook.chapter07.jaxb");
        return jaxb2Marshaller;
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

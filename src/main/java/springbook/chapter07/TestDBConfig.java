package springbook.chapter07;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springbook.chapter06.DummyMailSender;
import springbook.chapter07.sqlService.EmbeddedDbSqlRegistry;
import springbook.chapter07.sqlService.JaxbXmlSqlReader;
import springbook.chapter07.sqlService.OxmSqlService;
import springbook.chapter07.sqlService.SqlReader;
import springbook.chapter07.sqlService.SqlRegistry;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "springbook.chapter07")
public class TestDBConfig {

    private final String URL = "jdbc:mysql://localhost/springbook?characterEncoding=UTF-8";
    private final String ID = "spring";
    private final String PASSWORD = "book";

    @Autowired UserDao userDao;

    @Bean
    public UserService userOnlyTestServiceImpl() {
        TestUserService testService = new TestUserService();
        testService.setUserDao(this.userDao);
        testService.setMailSender(mailSenderImpl());
        return testService;
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
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry dbSqlRegistry = new EmbeddedDbSqlRegistry();
        dbSqlRegistry.setDataSource(embeddedDatabase());
        return dbSqlRegistry;
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
    public EmbeddedDatabase embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();
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

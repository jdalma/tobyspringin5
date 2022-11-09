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
import springbook.chapter07.sqlService.EmbeddedDbSqlRegistry;
import springbook.chapter07.sqlService.JaxbXmlSqlReader;
import springbook.chapter07.sqlService.OxmSqlService;
import springbook.chapter07.sqlService.SqlReader;
import springbook.chapter07.sqlService.SqlRegistry;

import javax.sql.DataSource;
import java.util.Properties;

//@Configuration
public class AppConfig {

}

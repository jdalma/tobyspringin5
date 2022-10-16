package springbook.chapter06.factoryBean;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import springbook.chapter06.AppConfig;
import springbook.chapter06.TestDBConfig;

import static org.assertj.core.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@ContextConfiguration(classes = AppConfig.class)
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    void getMessageFromFactoryBean() {
        Object message = context.getBean("messageFactoryBean");
        assertThat(message.getClass()).isEqualTo(Message.class);
        assertThat(((Message) message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    void getMessageThroughFactoryBean() {
        Object factoryBean = context.getBean("&messageFactoryBean");
        assertThat(factoryBean.getClass()).isEqualTo(MessageFactoryBean.class);
    }
}

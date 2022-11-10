package springbook.chapter07;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import springbook.chapter06.DummyMailSender;

@Configuration
@Profile("test")
public class TestAppContext {

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

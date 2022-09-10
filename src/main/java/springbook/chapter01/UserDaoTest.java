package springbook.chapter01;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class UserDaoTest {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao" , UserDao.class);
        System.out.println(dao);

        ConnectionMaker connectionMaker = context.getBean("connectionMaker" , ConnectionMaker.class);
        System.out.println(connectionMaker);
    }
}

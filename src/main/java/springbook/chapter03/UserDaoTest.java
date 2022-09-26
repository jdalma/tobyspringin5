package springbook.chapter03;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringJUnitConfig(DaoFactory.class)
@ContextConfiguration(classes = TestDBConfig.class)
public class UserDaoTest {

    @Autowired
    private UserDao dao;

    @BeforeEach
    void setUp() {

    }

    @Test
    void addAndGet() throws SQLException {


        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        User user1 = new User("test1" , "테스트1" , "password1");
        User user2 = new User("test2" , "테스트2" , "password2");

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        User findUser1 = dao.get(user1.getId());
        assertThat(findUser1.getName()).isEqualTo(user1.getName());
        assertThat(findUser1.getPassword()).isEqualTo(user1.getPassword());

        User findUser2 = dao.get(user2.getId());
        assertThat(findUser2.getName()).isEqualTo(user2.getName());
        assertThat(findUser2.getPassword()).isEqualTo(user2.getPassword());
    }

    @Test
    void count() throws SQLException {
        User user1 = new User("test1" , "테스트1" , "password1");
        User user2 = new User("test2" , "테스트2" , "password2");
        User user3 = new User("test3" , "테스트3" , "password3");

        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        assertThat(dao.getCount()).isEqualTo(1);

        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        dao.add(user3);
        assertThat(dao.getCount()).isEqualTo(3);
    }


    @Test
    void getUserFailure() throws SQLException {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> dao.get("unknown_id"))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}

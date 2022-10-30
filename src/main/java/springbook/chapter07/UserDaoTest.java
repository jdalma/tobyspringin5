package springbook.chapter07;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringJUnitConfig(AppConfig.class)
@ContextConfiguration(classes = TestDBConfig.class)
public class UserDaoTest {

    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;

    private final User user1 = new User("test1" , "테스트1" , "password1" , Level.BASIC, 1, 0, "test1");
    private final User user2 = new User("test2" , "테스트2" , "password2", Level.SILVER, 55, 10, "test2");
    private final User user3 = new User("test3" , "테스트3" , "password3", Level.GOLD, 100, 40, "test3");

    @Test
    void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("수정자1");
        user1.setPassword("수정자1");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        user1.setEmail("수정자1");
        dao.update(user1);

        User updatedUser1 = dao.get(user1.getId());
        checkSameUser(user1, updatedUser1);
        User updatedUser2 = dao.get(user2.getId());
        checkSameUser(updatedUser2, user2);
    }

    @Test
    void duplicateKey() {
        dao.deleteAll();


        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException ex) {
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            SQLException sqlEx = (SQLException) ex.getRootCause();

            assertThat(set.translate(null, null, sqlEx))
                    .isInstanceOf(DuplicateKeyException.class);
        }
    }

    @Test
    void addAndGet() {


        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount()).isEqualTo(2);

        User findUser1 = dao.get(user1.getId());
        checkSameUser(findUser1, user1);

        User findUser2 = dao.get(user2.getId());
        checkSameUser(findUser2, user2);
    }

    @Test
    void count() {
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
    void getUserFailure() {
        dao.deleteAll();
        assertThat(dao.getCount()).isEqualTo(0);

        assertThatThrownBy(() -> dao.get("unknown_id"))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void getAll() {
        dao.deleteAll();

        List<User> empty = dao.getAll();
        assertThat(empty.size()).isEqualTo(0);

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size()).isEqualTo(1);
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size()).isEqualTo(2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size()).isEqualTo(3);
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId()).isEqualTo(user2.getId());
        assertThat(user1.getName()).isEqualTo(user2.getName());
        assertThat(user1.getPassword()).isEqualTo(user2.getPassword());
        assertThat(user1.getLevel()).isEqualTo(user2.getLevel());
        assertThat(user1.getLogin()).isEqualTo(user2.getLogin());
        assertThat(user1.getRecommend()).isEqualTo(user2.getRecommend());
        assertThat(user1.getEmail()).isEqualTo(user2.getEmail());
    }
}

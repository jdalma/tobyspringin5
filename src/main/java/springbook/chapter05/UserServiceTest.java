package springbook.chapter05;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@ContextConfiguration(classes = TestDBConfig.class)
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("test1", "test1", "test1", Level.BASIC, 49, 0),
                new User("test2", "test2", "test2", Level.BASIC, 50, 0),
                new User("test3", "test3", "test3", Level.SILVER, 60, 29),
                new User("test4", "test4", "test4", Level.SILVER, 60, 30),
                new User("test5", "test5", "test5", Level.GOLD, 100, 100)
        );
    }

    @Test
    void upgradeLevels() {
        userDao.deleteAll();

        for(User user : users) {
            userDao.add(user);
        }

        userService.upgradeLevels();

        checkLevel(userDao.get("test1") , Level.BASIC);
        checkLevel(userDao.get("test2") , Level.SILVER);
        checkLevel(userDao.get("test3") , Level.SILVER);
        checkLevel(userDao.get("test4") , Level.GOLD);
        checkLevel(userDao.get("test5") , Level.GOLD);

    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        assertThat(userUpdate.getLevel()).isEqualTo(expectedLevel);
    }

    @Test
    void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }
}

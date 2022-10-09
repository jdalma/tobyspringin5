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
import static springbook.chapter05.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.chapter05.UserService.MIN_RECOMMEND_FOR_GOLD;

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
                new User("test1", "test1", "test1", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0),
                new User("test2", "test2", "test2", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0),
                new User("test3", "test3", "test3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
                new User("test4", "test4", "test4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
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

        checkLevelUpgraded(users.get(0) , false);
        checkLevelUpgraded(users.get(1) , true);
        checkLevelUpgraded(users.get(2) , false);
        checkLevelUpgraded(users.get(3) , true);
        checkLevelUpgraded(users.get(4) , false);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
            return;
        }
        assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
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

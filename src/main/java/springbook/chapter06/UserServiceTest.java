package springbook.chapter06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static springbook.chapter05.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.chapter05.UserService.MIN_RECOMMEND_FOR_GOLD;

@SpringJUnitConfig(AppConfig.class)
@ContextConfiguration(classes = TestDBConfig.class)
class UserServiceTest {

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MailSender mailSender;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("test1", "test1", "test1", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0, "test1"),
                new User("test2", "test2", "test2", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0, "test2"),
                new User("test3", "test3", "test3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "test3"),
                new User("test4", "test4", "test4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD , "test4"),
                new User("test5", "test5", "test5", Level.GOLD, 100, 100, "test5")
        );
    }

    @Test
    @DirtiesContext
    void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated).hasSize(2);
        checkUserAndLevel(updated.get(0), users.get(1).getId(), Level.SILVER);
        checkUserAndLevel(updated.get(1), users.get(3).getId(), Level.GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request).hasSize(2);
        assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
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

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    @Test
    void upgradeAllOrNothing() {
        UserServiceImpl testUserService = new UserServiceOnlyTest(users.get(3).getId());
        testUserService.setUserDao(this.userDao);
        testUserService.setMailSender(this.mailSender);

        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setUserService(testUserService);
        userServiceTx.setTransactionManager(this.transactionManager);

        this.userDao.deleteAll();
        for(User user : users) {
            this.userDao.add(user);
        }

        try {
            userServiceTx.upgradeLevels();
            fail("TestUserException expected");
        } catch (Exception e) {

        }

        checkLevelUpgraded(users.get(1) , false);
    }
}

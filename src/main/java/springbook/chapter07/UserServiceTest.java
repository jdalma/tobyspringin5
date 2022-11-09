package springbook.chapter07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static springbook.chapter07.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.chapter07.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@SpringJUnitConfig(classes = TestDBConfig.class)
@SpringBootApplication
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserService userOnlyTestServiceImpl;
    @Autowired
    private UserDao userDao;
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
    void upgradeLevels() {
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        UserDao mockUserDao = mock(UserDao.class);
        userServiceImpl.setUserDao(mockUserDao);
        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        given(mockUserDao.getAll()).willReturn(this.users);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);

        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender , times(2)).send(mailMessageArg.capture());

        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
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

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    @Test
    void upgradeAllOrNothing() throws Exception {
        this.userDao.deleteAll();
        for(User user : users) {
            this.userDao.add(user);
        }

        try {
            userOnlyTestServiceImpl.upgradeLevels();
            fail("TestUserException expected");
        } catch (Exception e) {

        }

        checkLevelUpgraded(users.get(1) , false);
    }

    @Test
    void readOnlyTransaction() {
        assertThatThrownBy(userOnlyTestServiceImpl::getAll)
                .isInstanceOf(TransientDataAccessResourceException.class);
    }
}

class TestUserService extends UserServiceImpl {
    private String id = "test3"; // users(3).getId()

    public void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

    public List<User> getAll() {
        for(User user : super.getAll()) {
            super.update(user);
        }
        return null;
    }
}

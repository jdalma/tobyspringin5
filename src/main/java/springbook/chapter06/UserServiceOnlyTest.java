package springbook.chapter06;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserServiceOnlyTest extends UserServiceImpl {

    private String id;

    public UserServiceOnlyTest(String id) {
        this.id = id;
    }

    @Override
    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            if (userLevelService.canUpgradeLevel(user)) {
                userLevelService.upgradeLevel(user);
            }
        }
    }
}

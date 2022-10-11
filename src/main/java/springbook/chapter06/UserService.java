package springbook.chapter06;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

public class UserService {

    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
    UserLevelUpgradePolicy userLevelService;
    PlatformTransactionManager transactionManager;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelService) {
        this.userLevelService = userLevelService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() {
        // 트랜잭션 시작
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            upgradeLevelsInternal();
            this.transactionManager.commit(status);
        } catch (RuntimeException ex) {
            this.transactionManager.rollback(status);
            throw ex;
        }
    }

    private void upgradeLevelsInternal() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            if (userLevelService.canUpgradeLevel(user)) {
                userLevelService.upgradeLevel(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}

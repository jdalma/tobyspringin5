package springbook.chapter05;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceOnlyTest extends UserService {

    private String id;

    public UserServiceOnlyTest(String id) {
        this.id = id;
    }

    @Override
    public void upgradeLevels() {
        // 트랜잭션 시작
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> users = userDao.getAll();
            for(User user : users) {
                if (user.getId().equals(this.id)) {
                    throw new TestUserServiceException();
                }
                if (userLevelService.canUpgradeLevel(user)) {
                    userLevelService.upgradeLevel(user);
                }
            }
            transactionManager.commit(status);
        } catch (RuntimeException ex) {
            transactionManager.rollback(status);
            throw ex;
        }
    }
}

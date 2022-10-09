package springbook.chapter05;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
    UserLevelUpgradePolicy userLevelService;
    DataSource dataSource;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelService) {
        this.userLevelService = userLevelService;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws Exception {
        // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화 한다.
        TransactionSynchronizationManager.initSynchronization();

        // DB 커넥션 생성, 트랜잭션 시작, 이후의 모든 데이터 접근 작업은 이 트랜잭션 안에서 진행된다.
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        try {
            List<User> users = userDao.getAll();
            for(User user : users) {
                if (userLevelService.canUpgradeLevel(user)) {
                    userLevelService.upgradeLevel(user);
                }
            }
            c.commit();
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            // 스프링 유틸리티 메소드를 이용해 DB 커넥션을 안전하게 닫는다.
            DataSourceUtils.releaseConnection(c, dataSource);

            // 동기화 작업 종료 및 해제
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}

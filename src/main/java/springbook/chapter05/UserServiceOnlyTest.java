package springbook.chapter05;

import org.springframework.jdbc.datasource.DataSourceUtils;
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
    public void upgradeLevels() throws SQLException {
        // 트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화 한다.
        TransactionSynchronizationManager.initSynchronization();

        // DB 커넥션 생성, 트랜잭션 시작, 이후의 모든 데이터 접근 작업은 이 트랜잭션 안에서 진행된다.
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

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
}

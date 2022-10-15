package springbook.chapter06;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class UserServiceTx implements UserService {
    UserService userService;
    PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        // 타깃 오브젝트
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // 메소드 구현과 위임
    @Override
    public void add(User user) {
        userService.add(user);
    }

    // 메소드 구현
    @Override
    public void upgradeLevels() {
        // 부가기능 수행
        TransactionStatus status =
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 위임
            userService.upgradeLevels();

            // 부가기능 수행
            this.transactionManager.commit(status);
        } catch (RuntimeException ex) {
            this.transactionManager.rollback(status);
            throw ex;
        }
    }

}

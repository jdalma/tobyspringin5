package springbook.chapter06;

import java.util.List;

public class UserOnlyTestServiceImpl extends UserServiceImpl {

    private String id = "test3";

    @Override
    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for(User user : users) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }
}

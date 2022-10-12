package springbook.chapter06;

import java.util.List;

public class UserServiceImpl implements UserService{

    UserDao userDao;
    UserLevelUpgradePolicy userLevelService;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelService) {
        this.userLevelService = userLevelService;
    }

    @Override
    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (userLevelService.canUpgradeLevel(user)) {
                userLevelService.upgradeLevel(user);
            }
        }
    }

    @Override
    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}

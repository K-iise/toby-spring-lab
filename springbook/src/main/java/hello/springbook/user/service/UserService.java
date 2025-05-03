package hello.springbook.user.service;

import hello.springbook.user.dao.UserDao;
import hello.springbook.user.domain.Level;
import hello.springbook.user.domain.User;

import java.util.List;


public class UserService {

    UserDao userDao;
    UserLevelUpgradePolicy userLevelUpgradePolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    // 사용자 레벨 업그레이드 메소드.
    public void upgradeLevels(){
        List<User> users = userDao.getAll();
        for (User user : users){
            if (userLevelUpgradePolicy.canUpgradeLevel(user))
                userLevelUpgradePolicy.upgradeLevel(user);
        }
    }

    public void add(User user){
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }


}

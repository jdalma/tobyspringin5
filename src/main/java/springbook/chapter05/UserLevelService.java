package springbook.chapter05;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static springbook.chapter05.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.chapter05.UserService.MIN_RECOMMEND_FOR_GOLD;

public class UserLevelService implements UserLevelUpgradePolicy {

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch(currentLevel) {
            case BASIC:
                return user.getLogin() >= MIN_LOGIN_COUNT_FOR_SILVER;
            case SILVER:
                return user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD;
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private void sendUpgradeEmail(User user) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "mail.ksug.org");
        Session s = Session.getInstance(props, null);

        MimeMessage message = new MimeMessage(s);
        try {
            message.setFrom(new InternetAddress("useradmin@ksug.org"));
            message.addRecipient(Message.RecipientType.TO,
                                new InternetAddress(user.getEmail()));
            message.setSubject("Upgrade 안내");
            message.setText(String.format("사용자님의 등급이 %s로 업그레이드 되었습니다.", user.getLevel().name()));

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

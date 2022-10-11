package springbook.chapter06;

import java.util.List;

public interface UserDao {
    int add(User user);
    int update(User user);
    User get(String id);
    List<User> getAll();
    int deleteAll();
    int getCount();
}

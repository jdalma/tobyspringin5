package springbook.chapter07;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    void add(User user);
    void deleteAll();
    void update(User user);

    @Transactional
    void upgradeLevels();

    @Transactional(readOnly = true)
    User get(String id);
    @Transactional(readOnly = true)
    List<User> getAll();
}

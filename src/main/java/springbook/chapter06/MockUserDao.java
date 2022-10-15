package springbook.chapter06;

import java.util.List;

public class MockUserDao implements UserDao {
    private List<User> users;
    private List<User> updated;

    private MockUserDao(List<User> users) {
        this.users = users;
    }

    public List<User> getUpdated() {
        return updated;
    }

    @Override
    public List<User> getAll() {
        return this.users;
    }

    @Override
    public int update(User user) {
        updated.add(user);
        return updated.size();
    }

    @Override
    public int add(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public User get(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int deleteAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
        throw new UnsupportedOperationException();
    }
}

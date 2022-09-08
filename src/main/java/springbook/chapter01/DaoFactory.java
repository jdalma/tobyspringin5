package springbook.chapter01;

public class DaoFactory {
    public UserDao userDao(){
        ConnectionMaker maker = new DConnectionMaker();
        return new UserDao(maker);
    }
}

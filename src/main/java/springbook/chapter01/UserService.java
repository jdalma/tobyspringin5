package springbook.chapter01;

public class UserService {
    public static void main(String[] args) throws Exception{
        UserDao dao = new UserDao();

        User user = new User();
        user.setId("jdalma1");
        user.setName("정현준");
        user.setPassword("test");

        dao.add(user);

        System.out.println(dao.get(user.getId()));
    }
}

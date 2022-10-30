package springbook.chapter07;

import java.util.HashMap;
import java.util.Map;

public class SimpleSqlService implements SqlService {

    private final Map<String , String> sqlMap;

    public SimpleSqlService() {
        sqlMap = new HashMap<>();
        sqlMap.put("userUpdate","update users set name = ?, password = ?, level = ?, login = ?,recommend = ?, email = ? where id = ?");
        sqlMap.put("userGetAll","select * from users order by id");
        sqlMap.put("userAdd","insert into users(id , name , password , level , login , recommend, email) values(?, ?, ?, ?, ?, ?, ?)");
        sqlMap.put("userDeleteAll","delete from users");
        sqlMap.put("userGet","select * from users where id = ?");
        sqlMap.put("userGetCount","select count(*) from users");
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
        }
        return sql;
    }
}

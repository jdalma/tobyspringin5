package springbook.chapter07.sqlService;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UserSqlMapConfig implements SqlMapConfig {
    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("sqlmap.xml");
    }
}
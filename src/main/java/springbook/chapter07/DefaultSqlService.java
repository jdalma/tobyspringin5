package springbook.chapter07;

import springbook.chapter07.sqlService.HashMapSqlRegistry;
import springbook.chapter07.sqlService.JaxbXmlSqlReader;
import springbook.chapter07.sqlService.SqlReader;
import springbook.chapter07.sqlService.SqlRegistry;

public class DefaultSqlService extends BaseSqlService {

    public DefaultSqlService() {
        setSqlReader(new JaxbXmlSqlReader());
        setSqlRegistry(new HashMapSqlRegistry());
    }
}

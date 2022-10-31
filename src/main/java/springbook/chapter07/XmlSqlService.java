package springbook.chapter07;

import springbook.chapter07.jaxb.SqlType;
import springbook.chapter07.jaxb.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlSqlService implements SqlService {

    private final Map<String , String> sqlMap = new HashMap<>();

    public XmlSqlService() {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            InputStream is = ClassLoader.class.getResourceAsStream("/sqlmap.xml");
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

            for (SqlType sql : sqlmap.getSql()) {
                this.sqlMap.put(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            // JAXBException은 복구 불가능한 예외이기 때문에 RuntimeException으로 포장해서 던진다
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
        }
        return sql;
    }
}

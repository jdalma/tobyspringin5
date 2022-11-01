package springbook.chapter07.sqlService;

import springbook.chapter07.jaxb.SqlType;
import springbook.chapter07.jaxb.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

public class JaxbXmlSqlReader implements SqlReader {

    private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
    private String sqlmapFile = DEFAULT_SQLMAP_FILE;

    @Override
    public void read(SqlRegistry sqlRegistry) {
        String contextPath = Sqlmap.class.getPackage().getName();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            InputStream is = ClassLoader.class.getResourceAsStream("/" + sqlmapFile);
            Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

            for (SqlType sql : sqlmap.getSql()) {
                sqlRegistry.registerSql(sql.getKey(), sql.getValue());
            }
        } catch (JAXBException e) {
            // JAXBException은 복구 불가능한 예외이기 때문에 RuntimeException으로 포장해서 던진다
            throw new RuntimeException(e);
        }
    }
}

package springbook.chapter07.sqlService;

import org.springframework.oxm.Unmarshaller;
import springbook.chapter07.BaseSqlService;
import springbook.chapter07.SqlRetrievalFailureException;
import springbook.chapter07.SqlService;
import springbook.chapter07.jaxb.SqlType;
import springbook.chapter07.jaxb.Sqlmap;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService {
    private final BaseSqlService baseSqlService = new BaseSqlService();
    private final SqlRegistry sqlRegistry = new HashMapSqlRegistry();

    // OxmSqlService와 OxmSqlReader는 강합게 결합되어 있다
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

    private static class OxmSqlReader implements SqlReader {
        private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
        private Unmarshaller unmarshaller;
        private String sqlmapFile = DEFAULT_SQLMAP_FILE;

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        public void setSqlmapFile(String sqlmapFile) {
            this.sqlmapFile = sqlmapFile;
        }

        @Override
        public void read(SqlRegistry sqlRegistry) {
            try {
                Source source = new StreamSource(this.getClass().getResourceAsStream("/" + sqlmapFile));
                Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(source);

                for (SqlType sql : sqlmap.getSql()) {
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PostConstruct
    public void loadSql() {
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);

        this.baseSqlService.loadSql();
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        return this.baseSqlService.getSql(key);
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    public void setSqlmapFile(String sqlmapFile) {
        this.oxmSqlReader.setSqlmapFile(sqlmapFile);
    }
}

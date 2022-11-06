package springbook.chapter07.sqlService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    EmbeddedDatabase db;

    @AfterEach
    void tearDown() {
        db.shutdown();
    }

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .build();

        EmbeddedDbSqlRegistry embeddedDbSQlRegistry = new EmbeddedDbSqlRegistry();
        embeddedDbSQlRegistry.setDataSource(db);

        return embeddedDbSQlRegistry;
    }

    @Test
    void transactionalUpdate() {
        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo("SQL1");
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo("SQL2");
        assertThat(sqlRegistry.findSql("KEY3")).isEqualTo("SQL3");

        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("Unknown Key" , "Modified2");

        try {
            sqlRegistry.updateSql(sqlmap);
            fail();
        } catch (SqlUpdateFailureException e) {

        }
        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo("SQL1");
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo("SQL2");
        assertThat(sqlRegistry.findSql("KEY3")).isEqualTo("SQL3");
    }
}

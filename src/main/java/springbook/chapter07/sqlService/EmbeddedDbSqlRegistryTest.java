package springbook.chapter07.sqlService;

import org.junit.jupiter.api.AfterEach;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    EmbeddedDatabase db;

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

    @AfterEach
    void tearDown() {
        db.shutdown();
    }
}

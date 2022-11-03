package springbook.chapter07.sqlService;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.object.SqlUpdate;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class ConcurrentHashMapSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp() {
        sqlRegistry = new ConcurrentHashMapSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    @Test
    void find() {
        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo("SQL1");
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo("SQL2");
        assertThat(sqlRegistry.findSql("KEY3")).isEqualTo("SQL3");
    }

    @Test
    void unknownKey() {
        assertThatThrownBy(() -> sqlRegistry.findSql("UNKNOWN"))
                .isInstanceOf(SqlNotFoundException.class);
    }

    @Test
    void updateSingle() {
        sqlRegistry.updateSql("KEY2", "Modified2");
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo("Modified2");
    }

    @Test
    void updateMulti() {
        Map<String,String> map = new HashMap<>();
        map.put("KEY1", "Modified1");
        map.put("KEY2", "Modified2");

        sqlRegistry.updateSql(map);

        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo("Modified1");
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo("Modified2");
    }

    @Test
    void updateWithNotExistingKey() {
        assertThatThrownBy(() -> sqlRegistry.updateSql("UNKNOWN" , "Modified"))
                .isInstanceOf(SqlUpdateFailureException.class);
    }
}

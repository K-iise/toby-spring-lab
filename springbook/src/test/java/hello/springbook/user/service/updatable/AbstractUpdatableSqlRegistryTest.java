package hello.springbook.user.service.updatable;

import hello.springbook.user.sqlservice.UpdatableSqlRegistry;
import hello.springbook.user.sqlservice.exception.SqlUpdateFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractUpdatableSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp(){
        sqlRegistry = createUpdatableSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

    protected void checkFind(String expected1, String expected2, String expected3){
        assertThat(sqlRegistry.findSql("KEY1")).isEqualTo(expected1);
        assertThat(sqlRegistry.findSql("KEY2")).isEqualTo(expected2);
        assertThat(sqlRegistry.findSql("KEY3")).isEqualTo(expected3);
    }

    @Test
    public void find() {
        checkFind("SQL1", "SQL2", "SQL3");
    }

    @Test
    public void updateSingle() throws SqlUpdateFailureException {
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFind("SQL1", "Modified2", "SQL3");
    }

    @Test
    public void updateMulti() throws SqlUpdateFailureException {
        Map<String, String> sqlMap = new HashMap<>();

        sqlMap.put("KEY1", "Modified1");
        sqlMap.put("KEY3", "Modified3");

        sqlRegistry.updateSql(sqlMap);
        checkFind("Modified1", "SQL2", "Modified3");
    }

    @Test
    public void updateWithNotExistingKey() {
        assertThrows(SqlUpdateFailureException.class, () -> sqlRegistry.updateSql("SQL9999!@#$", "Modified2"));
    }
}

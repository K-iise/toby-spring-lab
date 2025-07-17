package hello.springbook.user.service.updatable;

import hello.springbook.user.sqlservice.UpdatableSqlRegistry;
import hello.springbook.user.sqlservice.exception.SqlUpdateFailureException;
import hello.springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.HSQL;

public class EmbeddedDBSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
    EmbeddedDatabase db;

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        db = new EmbeddedDatabaseBuilder()
                .setType(HSQL)
                .addScript("classpath:sqlRegistrySchema.sql")
                .build();

        EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
        embeddedDbSqlRegistry.setDataSource(db);

        return embeddedDbSqlRegistry;
    }

    @AfterEach
    public void tearDown(){
        db.shutdown();
    }

    @Test
    public void transactionalUpdate()  {
        checkFind("SQL1", "SQL2", "SQL3");
        /* 초기 상태를 확인한다. 이미 슈퍼클래스의 다른 테스트 메소드에서 확인하긴 했지만
           트랜잭션 롤백 후의 결과와 비교돼서 이 테스트의 목적인 롤백 후의 상태는 처음과
           동일하다는 것을 비교해서 보여주려고 넣었다.
         */

        Map<String, String> sqlmap =  new HashMap<String, String>();
        sqlmap.put("KEY1", "Modified");
        sqlmap.put("KEY9999!@#$", "Modified9999");
        /* 두 번째 SQL의 키를 존재하지 않는 것으로 지정한다. 이 때문에 테스트는 실패할 것이고,
           그때 과연 롤백이 일어나는지 확인한다.
         */

        try {
            sqlRegistry.updateSql(sqlmap);
            Assertions.fail();
        } catch (SqlUpdateFailureException e) { }

        checkFind("SQL1", "SQL2", "SQL3");
    }
}

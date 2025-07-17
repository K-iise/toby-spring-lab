package hello.springbook.user.service.updatable;

import hello.springbook.user.sqlservice.updatable.ConcurrentHashMapSqlRegistry;
import hello.springbook.user.sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}

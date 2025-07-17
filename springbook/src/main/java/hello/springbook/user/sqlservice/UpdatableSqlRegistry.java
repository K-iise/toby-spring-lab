package hello.springbook.user.sqlservice;

import hello.springbook.user.sqlservice.exception.SqlUpdateFailureException;

import java.util.Map;

public interface UpdatableSqlRegistry extends SqlRegistry {
    public void updateSql(String key, String sql) throws SqlUpdateFailureException;

    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
}

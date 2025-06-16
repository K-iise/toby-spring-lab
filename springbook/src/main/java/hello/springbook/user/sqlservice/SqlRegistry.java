package hello.springbook.user.sqlservice;

import hello.springbook.user.sqlservice.exception.SqlNotFoundException;

public interface SqlRegistry {
    void registerSql(String key, String sql); // SQL을 키와 함께 등록한다
    String findSql(String key) throws SqlNotFoundException;
}

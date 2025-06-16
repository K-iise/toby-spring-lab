package hello.springbook.user.sqlservice;

import hello.springbook.user.sqlservice.exception.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}

package hello.springbook.user.sqlservice.updatable;

import hello.springbook.user.sqlservice.UpdatableSqlRegistry;
import hello.springbook.user.sqlservice.exception.SqlNotFoundException;
import hello.springbook.user.sqlservice.exception.SqlUpdateFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.Map;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
    JdbcTemplate jdbc;
    TransactionTemplate transactionTemplate; // JdbcTemplate와 트랜잭션을 동기화해주는 트랜잭션 템플릿이다. 멀티스레드 환경에서 공유 가능하다.
    public void setDataSource(DataSource dataSource){
        jdbc = new JdbcTemplate(dataSource);
        transactionTemplate = new TransactionTemplate(
                new DataSourceTransactionManager(dataSource)
        );
        /* dataSource로 TransactionManager를 만들고 이를 이용해서 TransactionTemplate를 생성한다.
         */
    }

    @Override
    public void registerSql(String key, String sql) {
        jdbc.update("insert into sqlmap(key_, sql_) values(?,?)", key, sql);
    }

    @Override
    public String findSql(String key) throws SqlNotFoundException {
        try {
            return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
        } catch (EmptyResultDataAccessException e){
            throw new SqlNotFoundException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }
    }

    @Override
    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);

        if (affected == 0){
            throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
        }
    }

    @Override
    public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
                    updateSql(entry.getKey(), entry.getValue());
                }
            }
        });

    }
}

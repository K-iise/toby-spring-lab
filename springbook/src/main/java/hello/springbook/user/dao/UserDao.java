package hello.springbook.user.dao;

import hello.springbook.user.domain.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.dataSource = dataSource;
    }

    private JdbcTemplate jdbcTemplate;

    public void add(final User user) throws SQLException {

        // 스프링의 템플릿/콜백 기술.
        this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
                user.getId(), user.getName(), user.getPassword());

        /*// 익명 내부 클래스를 적용.
        this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
                ps.setString(1, user.getId());
                ps.setString(2, user.getName());
                ps.setString(3, user.getPassword());

                return ps;
            }
        });*/
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?"
        );

        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        // 결과가 없으면 User는 null 상태 그대로 일것이다. 이를 확인해서 예외를 던져준다.
        if(user == null) throw new EmptyResultDataAccessException(1);

        return user;

    }

    public void deleteAll() throws SQLException{
        /* JdbcTemplate의 템플릿 메소드를 사용해서 콜백 구현.
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("delete from users");
            }
        });*/
        // 내장 콜백을 사용해서 deleteAll 실행.
        this.jdbcTemplate.update("delete from users");
    }

    /*
    jdbcContext에서 구현한 콜백.
    private void executeSql(final String query) throws SQLException {
        this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
            @Override
            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
                return c.prepareStatement(query);
            }
        });
    }
    */

    public int getCount() throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e){
            throw e;
        } finally {
            if(rs != null){
                try {
                    rs.close();
                } catch ( SQLException e){

                }
            }

            if(ps != null){
                try {
                    ps.close();
                } catch (SQLException e){

                }
            }

            if(c != null){
                try {
                    c.close();
                } catch (SQLException e){

                }
            }
        }
    }

    // 전략 패턴에서 컨텍스트(Context)
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException{
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (ps != null){
                try {
                    ps.close();
                }
                catch (SQLException e){
                }
            }

            if (c != null){
                try {
                    c.close();
                } catch (SQLException e){

                }
            }
        }
    }

}

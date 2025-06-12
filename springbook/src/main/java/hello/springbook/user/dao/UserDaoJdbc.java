package hello.springbook.user.dao;

import hello.springbook.user.domain.Level;
import hello.springbook.user.domain.User;
import hello.springbook.user.sqlservice.SqlService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao{

    private SqlService sqlService;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLogin(rs.getInt("login"));
            user.setRecommend(rs.getInt("recommend"));
            user.setEmail(rs.getString("email"));
            return user;
        }
    };

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    @Override
    public void add(final User user) {
        // 스프링의 템플릿/콜백 기술.
        this.jdbcTemplate.update(this.sqlService.getSql("add"),
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(),
                user.getLogin(), user.getRecommend(), user.getEmail());
    }

    @Override
    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("get"), new Object[]{id}, this.userMapper);
    }

    @Override
    public void deleteAll() {
        // 내장 콜백을 사용해서 deleteAll 실행.
        this.jdbcTemplate.update(this.sqlService.getSql("deleteAll"));
    }

    @Override
    public int getCount() {
        // jdbcTemplate에서 queryForInt는 더이상 사용 X
        // 따라서 queryForObject에서 쿼리문, 타입을 매개변수로 줘서 대체함.
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("getCount"), Integer.class);
    }

    @Override
    public List<User> getAll(){
        return this.jdbcTemplate.query(this.sqlService.getSql("getAll"), this.userMapper);
    }

    @Override
    public void update(User user1) {
        this.jdbcTemplate.update(this.sqlService.getSql("update"),
                user1.getName(), user1.getPassword(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getEmail(),
                user1.getId());
    }
}

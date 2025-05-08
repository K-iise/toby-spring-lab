package hello.springbook.dao;

import hello.springbook.user.dao.CountingConnectionMaker;
import hello.springbook.user.dao.CountingDaoFactory;
import hello.springbook.user.dao.UserDao;
import hello.springbook.user.domain.Level;
import hello.springbook.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {

    @Test
    @DisplayName("CountingConnectionMaker에 대한 테스트 클래스")
    void countingConnectionTest() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao dao = context.getBean(UserDao.class);

        User user = new User();
        user.setId("tjised");
        user.setName("홍길동");
        user.setPassword("1122");
        user.setLevel(Level.GOLD);
        user.setLogin(100);
        user.setRecommend(30);
        dao.add(user);

        User whiteship = dao.get("whiteship");

        CountingConnectionMaker ccm = context.getBean(CountingConnectionMaker.class);
        System.out.println("Connection counter : " + ccm.getCounter());
    }
}

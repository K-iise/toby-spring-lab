package hello.springbook;

import hello.springbook.user.dao.DaoFactory;
import hello.springbook.user.dao.UserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DaoTest {
    @Test
    @DisplayName("직접 생성한 DaoFactory 오브젝트 출력 코드")
    public void daoIdentify(){
        DaoFactory factory = new DaoFactory();
        UserDao dao1 = factory.userDao();
        UserDao dao2 = factory.userDao();

        System.out.println(dao1);
        System.out.println(dao2);
    }

    @Test
    @DisplayName("스프링 컨텍스트로부터 가져온 오브젝트 출력 코드")
    public void daoApplicationContext(){
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDao dao3 = context.getBean("userDao", UserDao.class);
        UserDao dao4 = context.getBean("userDao", UserDao.class);

        System.out.println(dao3);
        System.out.println(dao4);
        System.out.println(dao3 == dao4);
    }
}

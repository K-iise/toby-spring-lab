package hello.springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {

    @Bean
    public UserDaoJdbc userDao(){
        return new UserDaoJdbc();
    }

    @Bean
    public ConnectionMaker connectionMaker(){
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    private static DconnectionMaker realConnectionMaker() {
        return new DconnectionMaker();
    }
}

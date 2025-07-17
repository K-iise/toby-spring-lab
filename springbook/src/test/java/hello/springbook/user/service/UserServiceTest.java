package hello.springbook.user.service;

import hello.springbook.user.dao.UserDao;
import hello.springbook.user.domain.Level;
import hello.springbook.user.domain.User;
import hello.springbook.user.service.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static hello.springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static hello.springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {


    @Autowired
    ApplicationContext context;

    @Autowired
    MailSender mailSender;
    @Autowired
    PlatformTransactionManager transactionManager;

    @Autowired
    UserService userService;
    @Autowired
    UserService testUserService;

    @Autowired
    UserDao userDao;

    List<User> users;
    @BeforeEach
    public void setUp(){
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "test@test.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "test@test.com"),
                new User("erwins", "신승환", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, "test@test.com"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "test@test.com"),
                new User("green", "오민규", "p5",Level.GOLD, 100, Integer.MAX_VALUE, "test@test.com")
        );
    }

    @Test
    @DisplayName("사용자 레벨 업그레이드 테스트")
    public void upgradeLevels() throws Exception {

        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);
    }

    @Test
    @DisplayName("add() 메소드의 테스트")
    public void add() {
        userDao.deleteAll();
        User userWithLevel = users.get(4); // GOLD 레벨
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null); // 레벨이 비어 있는 사용자. 로직에 따라 등록 중에 BASIC 레벨로 설정되어야 함.

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelRead.getLevel()).isEqualTo(Level.BASIC);
    }

    @Test
    @DirtiesContext
    @DisplayName("예외 발생 시 작업 취소 여부 테스트")
    public void upgradeAllorNothing() throws Exception {

        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        try {
            this.testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (Exception e){

        }

        checkLevelUpgraded(users.get(1), false);
    }

    @Test
    @DisplayName("메일 발송 대상을 확인하는 테스트")
    @DirtiesContext
    public void upgradeLevelsSendMail() throws Exception{
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size()).isEqualTo(2);
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);

        List<String> request = mockMailSender.getRequest();
        assertThat(request.size()).isEqualTo(2);
        assertThat(request.get(0)).isEqualTo(users.get(1).getEmail());
        assertThat(request.get(1)).isEqualTo(users.get(3).getEmail());
    }

    @Test
    @DisplayName("Mockito를 적용한 테스트 코드")
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();
        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel()).isEqualTo(Level.SILVER);
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel()).isEqualTo(Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0]).isEqualTo(users.get(1).getEmail());
        assertThat(mailMessages.get(1).getTo()[0]).isEqualTo(users.get(3).getEmail());
    }

    @Test
    @DisplayName("다이내믹 프록시를 이용한 트랜잭션 테스트")
    public void upgradeAllorNothingProxy(){
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(testUserService);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern("upgradeLevels");

        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        UserService txUserService = (UserService) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] {UserService.class}, txHandler
        );

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (Exception e){

        }

        checkLevelUpgraded(users.get(1), false);
    }
    
    @Test
    @DisplayName("읽기전용 속성 테스트")
    public void readOnlyTransactionAttribut() {
        org.junit.jupiter.api.Assertions.assertThrows(TransientDataAccessException.class, ()->testUserService.getAll());
    }

    @Test
    @DisplayName("트랜잭션의 롤백 테스트")
    public void transactionSync(){
        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        try {
            userService.deleteAll();
            userService.add(users.get(0));
            userService.add(users.get(1));
        }
        finally {
            transactionManager.rollback(txStatus);
        }

        assertThat(userDao.getCount()).isEqualTo(5);
    }

    @Test
    @Transactional // 기본적으로 트랜잭션을 강제로 롤백시키도록 설정되어 있다.
    @Rollback(value = false)
    @DisplayName("테스트에 적용된 @Transactional")
    public void transactionSync2(){
        userService.deleteAll();
        userService.add(users.get(0));
        userService.add(users.get(1));
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
        assertThat(updated.getId()).isEqualTo(expectedId);
        assertThat(updated.getLevel()).isEqualTo(expectedLevel);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().nextLevel());
        } else {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }

    // 읽기전용 메소드에 쓰기 작업을 추가한 테스트용 클래스.
    static class TestUserService extends UserServiceImpl {
        private String id = "madnite1";

        @Override
        protected void upgradeLevel(User user){
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }

        @Override
        public List<User> getAll() {
            for (User user : super.getAll()){
                super.update(user);
            }
            return null;
        }
    }

    static class TestUserServiceException extends RuntimeException{

    }

    static class MockMailSender implements MailSender {
        private List<String> requests = new ArrayList<String>();

        public List<String> getRequest() {
            return requests;
        }

        @Override
        public void send(SimpleMailMessage mailMessage) throws MailException {
            requests.add(mailMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage[] mailMessage) throws MailException {
        }
    }

    static class MockUserDao implements UserDao {
        private List<User> users;
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated(){
            return this.updated;
        }

        public List<User> getAll(){
            return this.users;
        }

        public void update(User user){
            updated.add(user);
        }

        public void add(User user) {throw new UnsupportedOperationException();}
        public void deleteAll() { throw new UnsupportedOperationException();}
        public User get(String id) { throw new UnsupportedOperationException();}
        public int getCount() {throw new UnsupportedOperationException();}
    }
}

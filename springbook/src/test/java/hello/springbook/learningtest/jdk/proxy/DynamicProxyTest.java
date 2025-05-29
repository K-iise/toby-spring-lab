package hello.springbook.learningtest.jdk.proxy;

import hello.springbook.user.service.UserService;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class DynamicProxyTest {

    @Autowired
    UserService testUserService;

    @Test
    public void proxyFactoryBean(){
        ProxyFactory pfBean = new ProxyFactory();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvise());

        Hello proxieHello = (Hello) pfBean.getProxy();

        assertThat(proxieHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxieHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxieHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    @Test
    @DisplayName("포인트컷까지 적용한 proxyFactoryBean")
    public void pointcutAdviser(){
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        // 메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷 생성.
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*"); // 이름 비교조건 설정. sayH로 시작하는 모든 메소드를 선택하게 한다.

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvise()));

        Hello proxieHello = (Hello) pfBean.getObject();

        assertThat(proxieHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxieHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxieHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    }

    @Test
    @DisplayName("확장된 포인트컷 테스트")
    public void classNamePointcutAdvisor(){
        // 포인트컷 준비
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            @Override
            public ClassFilter getClassFilter() { // 익명 내부 클래스 방식으로 클래스를 정의한다.
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT"); // 클래스 이름이 HelloT로 시작하는 것만 선정한다.
                    }
                };
            }
        };
        classMethodPointcut.setMappedName("sayH*"); // sayH로 시작하는 메소드 이름을 가진 메소드만 선정한다.

        // 테스트
        checkAdviced(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget {};
        checkAdviced(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget {};
        checkAdviced(new HelloToby(), classMethodPointcut, true);
    }

    @Test
    @DisplayName("자동생성된 프록시 확인")
    public void advisorAutProxyCreator(){
        assertThat(testUserService).isInstanceOf(java.lang.reflect.Proxy.class);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvise()));
        Hello proxieHello = (Hello) pfBean.getObject();

        if(adviced) {
            assertThat(proxieHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
            assertThat(proxieHello.sayHi("Toby")).isEqualTo("HI TOBY");
            assertThat(proxieHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
        }
        else {
            assertThat(proxieHello.sayHello("Toby")).isEqualTo("Hello Toby");
            assertThat(proxieHello.sayHi("Toby")).isEqualTo("Hi Toby");
            assertThat(proxieHello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
        }
    }

    static class UppercaseAdvise implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }
    }

    static interface Hello{
        String sayHello(String name);
        String sayHi(String name);
        String sayThankYou(String name);
    }

    static class HelloTarget implements Hello {
        @Override
        public String sayHello(String name) {
            return "Hello " + name;
        }

        @Override
        public String sayHi(String name) {
            return "Hi " + name;
        }

        @Override
        public String sayThankYou(String name) {
            return "Thank You " + name;
        }
    }
}

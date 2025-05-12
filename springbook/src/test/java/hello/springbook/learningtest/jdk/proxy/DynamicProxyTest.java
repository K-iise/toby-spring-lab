package hello.springbook.learningtest.jdk.proxy;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import static org.assertj.core.api.Assertions.*;

public class DynamicProxyTest {

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

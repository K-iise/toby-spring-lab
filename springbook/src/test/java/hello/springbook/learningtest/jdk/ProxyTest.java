package hello.springbook.learningtest.jdk;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.*;

public class ProxyTest {
    @Test
    @DisplayName("클라이언트 역할의 테스트")
    public void simpleProxy(){
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello("Toby")).isEqualTo("Hello Toby");
        assertThat(hello.sayHi("Toby")).isEqualTo("Hi Toby");
        assertThat(hello.sayThankYou("Toby")).isEqualTo("Thank You Toby");
    }

    @Test
    @DisplayName("HelloUppercase 프록시 테스트")
    public void upperProxy(){
        Hello proxiedHello = new HelloUppercase(new HelloTarget());
        assertThat(proxiedHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxiedHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxiedHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

    @Test
    @DisplayName("다이나믹 프록시 테스트")
    public void dynamicProxy(){
        Hello proxieHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        assertThat(proxieHello.sayHello("Toby")).isEqualTo("HELLO TOBY");
        assertThat(proxieHello.sayHi("Toby")).isEqualTo("HI TOBY");
        assertThat(proxieHello.sayThankYou("Toby")).isEqualTo("THANK YOU TOBY");
    }

}

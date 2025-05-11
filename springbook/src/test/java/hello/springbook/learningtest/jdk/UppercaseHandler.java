package hello.springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class UppercaseHandler implements InvocationHandler {
    Object target;

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object ret = method.invoke(target,args);

        // 메소드를 선별해서 부가기능을 적용하는 invoke() 방식.
        // 리턴 타입과 메소드 이름이 일치하는 경우에만 부가기능을 적용한다.
        if(ret instanceof String && method.getName().startsWith("say")){
            return (((String) ret).toUpperCase());
        }
        else
            return ret;
    }
}

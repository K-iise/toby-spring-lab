package hello.springbook.user.learningtest.spring.pointcut;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.assertj.core.api.Assertions.*;

public class PointcutExpressionTest {

    @Test
    @DisplayName("메소드 시그니처를 이용한 포인트컷 표현식 테스트")
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int hello.springbook.user.learningtest.spring.pointcut.Target.minus(int,int))");
        // Target 클래스 minus() 메소드 시그니처

        // Target.minus()
        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null)).isTrue();

        // Target.plus()
        assertThat(pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null)).isFalse();

        // Bean.method()
        assertThat(pointcut.getClassFilter().matches(Bean.class) &&
                pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null)).isFalse();
    }

    @Test
    @DisplayName("포인트컷 표현식 테스트")
    public void pointcut() throws Exception {
        targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
        // 나머지는 생략 ~ 표 6-1의 내용과 동일하다.
    }


    // 포인트컷과 메소드를 비교해주는 테스트 헬퍼 메소드
    public void pointcutMatches(String expression, Boolean expected, Class<?> clazz,
                                String methodName, Class<?>... args) throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);

        assertThat(pointcut.getClassFilter().matches(clazz) &&
                pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null)).isEqualTo(expected);
    }

    // 타킷 글래스의 메소드 6개에 대해 포인트컷 선정 여부를 검사하는 헬퍼 메소드
    public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception {
        pointcutMatches(expression, expected[0], Target.class, "hello");
        pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
        pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
        pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
        pointcutMatches(expression, expected[4], Target.class, "method");
        pointcutMatches(expression, expected[5], Bean.class, "method");
    }
}

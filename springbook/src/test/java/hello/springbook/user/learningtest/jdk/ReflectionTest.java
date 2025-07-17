package hello.springbook.user.learningtest.jdk;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

public class ReflectionTest {
    @Test
    @DisplayName("리플렉션 학습 테스트")
    public void invokeMethod() throws Exception {
        String name = "Spring";

        // length()
        assertThat(name.length()).isEqualTo(6);

        Method lengthMethod = String.class.getMethod("length");
        assertThat((Integer)lengthMethod.invoke(name)).isEqualTo(6);

        // chatAt()
        assertThat(name.charAt(0)).isEqualTo('S');

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertThat((Character) charAtMethod.invoke(name,0)).isEqualTo('S');
    }
}

package hello.springbook.user.learningtest.template;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class CalcSumTest {

    Calculator calculator;;
    String numFilepath;

    @BeforeEach
    public void setup(){
        this.calculator = new Calculator();
        this.numFilepath = "C:\\Users\\kimyu\\OneDrive\\바탕 화면\\스프링 공부\\toby-spring-lab\\springbook\\src\\test\\resources\\numbers.txt";
    }

    @Test
    @DisplayName("덧셈을 계산하는 메소드")
    public void sumOfNumbers() throws IOException{
        Assertions.assertThat(calculator.calcSum(numFilepath)).isEqualTo(10);
    }

    @Test
    @DisplayName("곱을 계산하는 메소드")
    public void multiplyOfNumbers() throws IOException{
        Assertions.assertThat(calculator.calcMultiply(numFilepath)).isEqualTo(24);
    }

    @Test
    @DisplayName("문자열을 연결하는 메소드")
    public void concatenate() throws IOException{
        Assertions.assertThat(calculator.concatenate(numFilepath)).isEqualTo("1234");
    }
}

package hello.springbook.user.learningtest.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class JUnitTest {

    @Autowired
    ApplicationContext context;
    static Set<JUnitTest> testObject = new HashSet<JUnitTest>();
    static ApplicationContext contextObject = null;
    @Test
    public void test1(){
        assertThat(testObject).doesNotContain(this);
        testObject.add(this);
        if(contextObject == null){
            contextObject = this.context;
        } else {
            assertThat(this.context).isSameAs(contextObject);
        }
    }

    @Test
    public void test2(){
        assertThat(testObject).doesNotContain(this);
        testObject.add(this);
        if(contextObject == null){
            contextObject = this.context;
        } else {
            assertThat(this.context).isSameAs(contextObject);
        }
    }

    @Test
    public void test3(){
        assertThat(testObject).doesNotContain(this);
        testObject.add(this);
        if(contextObject == null){
            contextObject = this.context;
        } else {
            assertThat(this.context).isSameAs(contextObject);
        }
    }
}

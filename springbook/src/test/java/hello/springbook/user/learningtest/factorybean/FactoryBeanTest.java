package hello.springbook.user.learningtest.factorybean;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/FactoryBeanText-Context.xml")
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    @DisplayName("팩토리 빈 테스트")
    public void getMessageFromFactoryBean(){
        // 빈 오브젝트 자체를 가져옴.
        Object message = context.getBean("message");
        assertThat(message).isInstanceOf(Message.class);
        assertThat(((Message)message).getText()).isEqualTo("Factory Bean");
    }

    @Test
    @DisplayName("팩토리 빈을 가져오는 기능 테스트")
    public void getFactoryBean() throws Exception {
        // &가 붙고 안 붙고에 따라 getBean() 메소드가 돌려주는 오브젝트가 달라진다.
        Object factory = context.getBean("&message");
        assertThat(factory).isInstanceOf(MessageFactoryBean.class);
    }
}

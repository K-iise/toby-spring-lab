package hello.springbook.user.learningtest.jdk.jaxb;

import hello.springbook.user.sqlservice.jaxb.SqlType;
import hello.springbook.user.sqlservice.jaxb.Sqlmap;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class JaxbTest {
    @Test
    @DisplayName("JAXB 학습 테스트")
    public void readSqlmap() throws JAXBException, IOException {
        String contextPath = Sqlmap.class.getPackage().getName();
        JAXBContext context = JAXBContext.newInstance(contextPath);
        // 바인딩용 클래스를 위치를 가지고 JAXB 컨텍스트를 만든다.

        Unmarshaller unmarshaller = context.createUnmarshaller();
        // 언마살러 생성

        System.out.println(getClass().getResource("/sqlmap.xml"));

        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(
                getClass().getResourceAsStream("/sqlmap.xml")
        );

        List<SqlType> sqlList = sqlmap.getSql();

        Assertions.assertThat(sqlList.size()).isEqualTo(3);
        Assertions.assertThat(sqlList.get(0).getKey()).isEqualTo("add");
        Assertions.assertThat(sqlList.get(0).getValue()).isEqualTo("insert");
        Assertions.assertThat(sqlList.get(1).getKey()).isEqualTo("get");
        Assertions.assertThat(sqlList.get(1).getValue()).isEqualTo("select");
        Assertions.assertThat(sqlList.get(2).getKey()).isEqualTo("delete");
        Assertions.assertThat(sqlList.get(2).getValue()).isEqualTo("delete");
    }
}

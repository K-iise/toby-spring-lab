package hello.springbook.learningtest.spring.oxm;


import hello.springbook.user.sqlservice.jaxb.SqlType;
import hello.springbook.user.sqlservice.jaxb.Sqlmap;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "/OxmTest-context.xml")
public class OxmTest {
    @Autowired
    Unmarshaller unmarshaller;


    @Test
    @DisplayName("OXM 언마샬링 테스트 코드")
    public void unmarshallSqlMap() throws XmlMappingException, IOException  {
        Source xmlSource = new StreamSource(
                getClass().getResourceAsStream("/sqlmap.xml")
        );
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);

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

package hello.springbook.user.sqlservice;

import hello.springbook.user.dao.UserDao;
import hello.springbook.user.sqlservice.exception.SqlNotFoundException;
import hello.springbook.user.sqlservice.exception.SqlRetrievalFailureException;
import hello.springbook.user.sqlservice.jaxb.SqlType;
import hello.springbook.user.sqlservice.jaxb.Sqlmap;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;

public class OxmSqlService implements SqlService {
    private final BaseSqlService baseSqlService =  new BaseSqlService();
    private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

    private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
    /* oxmSqlReader와 달리 단지 디폴트 오브젝트로 만들어진 프로퍼티다.
    *  따라서 필요에 따라 DI를 통해 교체 가능하다. */

    public void setSqlRegistry(SqlRegistry sqlRegistry) {
        this.sqlRegistry = sqlRegistry;
    }

    public void setSqlmap(Resource sqlmap) {
        this.oxmSqlReader.setSqlmap(sqlmap);
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.oxmSqlReader.setUnmarshaller(unmarshaller);
    }

    @PostConstruct
    public void loadSql() {
        this.baseSqlService.setSqlReader(this.oxmSqlReader);
        this.baseSqlService.setSqlRegistry(this.sqlRegistry);

        this.baseSqlService.loadSql();
        // SQL을 등록하는 초기화 작업을 baseSqlService에 위임한다.
    }

    public String getSql(String key) throws SqlRetrievalFailureException {
        return this.baseSqlService.getSql(key);
    }

    private class OxmSqlReader implements SqlReader {
        private Unmarshaller unmarshaller;
        private Resource sqlmap = new ClassPathResource("main-sqlmap.xml", UserDao.class);

        public void setSqlmap(Resource sqlmap) {
            this.sqlmap = sqlmap;
        }

        public void setUnmarshaller(Unmarshaller unmarshaller) {
            this.unmarshaller = unmarshaller;
        }

        @Override
        public void read(SqlRegistry sqlRegistry) {
            try {
                Source source = new StreamSource(sqlmap.getInputStream());
                Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

                for (SqlType sql : sqlmap.getSql()){
                    sqlRegistry.registerSql(sql.getKey(), sql.getValue());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다.", e);
            }
        }
    }
}

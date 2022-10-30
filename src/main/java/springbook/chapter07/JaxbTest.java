package springbook.chapter07;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import springbook.chapter07.jaxb.SqlType;
import springbook.chapter07.jaxb.Sqlmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class JaxbTest {

    @Test
    void readSqlmap() throws JAXBException {
        String contextPath = Sqlmap.class.getPackage().getName();
        // 바인딩용 클래스 위치를 가지고 JAXB컨텍스트를 만든다.
        JAXBContext context = JAXBContext.newInstance(contextPath);
        // 언마샬러 생성
        Unmarshaller unmarshaller = context.createUnmarshaller();
        // 언마샬을 하면 매핑된 오브젝트 트리의 루트인 Sqlmap을 돌려준다
        Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("/sqlmap.xml"));

        List<SqlType> sqlList = sqlmap.getSql();

        assertThat(sqlList).hasSize(3);
        assertThat(sqlList.get(0).getKey()).isEqualTo("add");
        assertThat(sqlList.get(0).getValue()).isEqualTo("insert");

        assertThat(sqlList.get(1).getKey()).isEqualTo("get");
        assertThat(sqlList.get(1).getValue()).isEqualTo("select");

        assertThat(sqlList.get(2).getKey()).isEqualTo("delete");
        assertThat(sqlList.get(2).getValue()).isEqualTo("delete");
    }
}

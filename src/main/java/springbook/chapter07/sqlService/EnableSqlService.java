package springbook.chapter07.sqlService;

import org.springframework.context.annotation.Import;
import springbook.chapter07.SqlServiceContext;

@Import(value = SqlServiceContext.class)
public @interface EnableSqlService {
}

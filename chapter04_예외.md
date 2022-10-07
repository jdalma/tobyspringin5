
**JdbcTemplate**을 대표로 하는 **스프링의 데이터 엑세스 기능에 담겨 있는 예외 처리와 관련된 접근 방법에 대해 알아보자.**

```
예외를 처리할 때 반드시 지켜야 할 핵심 원칙은 한 가지다.
모든 예외는 적절하게 복구되든지 아니면 작업을 중단시키고 운영자 또는 개발자에게 분명하게 통보돼야 한다.

자바 언어와 JDK 초기 설계자들은 체크 예외를 발생 가능한 예외에 모두 적용하려고 했던 것 같다.
예외적인 상황에서 던져질 가능성이 있는 것들 대부분이 체크 예외로 만들어져 있다.
체크 예외의 불필요성을 주장하는 사람들이 늘어갔고, 예외 블랙홀이나 무책임한 throws를 남발하게 되었다.
최근에 새로 등장하는 자바 표준 스펙의 API 또는 오픈 소스 프레임워크들은 예상 가능한 예외상황을 다루는 예외를 체크 예외 대신 언체크 예외로 만드는 경향이 있다.

애플리케이션 로직상에서 예외조건이 발견되거나 예외상황이 발생할 수도 있다.
이런 것은 의도적으로 던지는 예외이기 때문에 "체크 예외"를 사용하는 것이 적절하다.
비즈니스적인 의미가 있는 예외는 이에 대한 적절한 대응이나 복구작업이 필요하기 때문이다.

런타임 예외 중심의 전략은 "낙관적인 예외처리 기법"이라고 할 수 있다.
복구할 수 있는 예외는 없다고 가정하고 예외가 생겨도 런타임 예외 이므로 시스템 레벨에서 알아서 처리해줄 것이고,
꼭 필요한 경우는 런타임 예외라도 잡아서 복구하거나 대응해줄 수 있으니 문제 될 겂이 없다는 낙관적인 태도를 기반으로 하고 있다.
이런 면에서 직접 처리할 수 없는 예외가 대부분이라고 하더라도 혹시 놓치는 예외가 있을 수 있으니, 일단 잡도록 강제하는 체크 예외의 "비관적인 접근 방법"과 대비된다. 
```

# **4.1 사라진 SQLException**

```java
// JdbcTemplate 적용 전
public void deleteAll() throws SQLException {
    this.jdbcContext.executeSql("delete from users");
}

// JdbcTemplate 적용 후
public void deleteAll() {
    this.jdbcTemplate.update("delete from users");
}
```

**JdbcTemplate** 적용 후에는 **SQLException**이 사라졌다.  
이 **SQLException** 어디로 사라진걸까?  

## 4.1.1 초난감 예외처리 ⭐️

### **예외를 잡아서 무시하거나 잡아먹어 버리는 코드는 만들지 마라**

예외를 잡아서 초치를 취할 방법이 없다면 잡지 말아야 한다. <br>
메소드에 `throws SQLException`을 선언해서 메소드 밖으로 던지고 자신을 호출한 코드에게 예외를 전가해버려라.

### **무의미하고 무책임한 `throws`**

`throws Exception`을 기계적으로 붙이는 개발자도 있다. <br>
위와 같은 무책임한 예외 전가는 개발자에게 의미있는 정보를 전달할 수 없다. <br>

**정말 무엇인가 실행 중에 예외적인 상황이 발생할 수 있다는 것인지, 그냥 복사해서 붙여놓은 것인지 알 수가 없다.** <br>
결국 무책임한 예외를 전가하는 메소드를 사용하는 메소드 역시 `throws Exception`을 따라 붙일 수 밖에 없다. <br>
결과적으로 **적절한 처리를 통해 복구될 수 있는 예외상황도 제대로 다룰 수 있는 기회를 박탈당한다.**

## 4.1.2 예외의 종류와 특징 ⭐️

예외를 어떻게 처리해야할까? <br>
가장 큰 이슈는 **체크 예외 `checked exception`** 라고 불리는 **명시적인 처리가 필요한 예외**를 사용하고 다루는 방법이다. <br>
`throw`를 통해 발생시킬 수 있는 예외는 크게 세 가지다.

1. **Error**
   - `java.lang.Error` 클래스의 서브 클래스들
   - 시스템에 뭔가 비정상적인 상황이 발생했을 경우
   - 주로 자바 VM에서 발생시키는 것이고 애플리케이션 코드에서 잡으려고 하면 안된다.
   - 따라서 **애플리케이션에서는 이런 에러에 대한 처리는 신경쓰지 않아도 된다.**
2. **Exception과 체크 예외**
   - `java.lang.Exception` 클래스와 그 서브클래스
   - `Error`와 달리 개발자들이 만든 애플리케이션 코드의 작업 중에 예외상황이 발생했을 경우에 사용된다.
   - `Exception` 클래스는 **체크 예외**와 **언체크 예외**로 구분된다.
   - **체크 예외** : `Exception`클래스의 서브 클래스이면서 `RuntimeException`을 상속하지 않은 것들
   - **언체크 예외** : `RuntimeException`을 상속한 클래스
   - 일반적으로 예외라고하면 **체크 예외**라고 생각하면 된다.
3. **RuntimeException과 언체크/런타임 예외**
   - `java.lang.RuntimeException` 클래스를 상속한 예외들
   - 주로 프로그램의 오류가 있을 때 발생하도록 의도된 것들
   - 대표적으로 NullPointerException, IllegalArgumentException 등

## 4.1.3 예외처리 방법 ⭐️

1. **예외복구**
   - 예외상황을 파악하고 문제를 해결해서 정상 상태로 돌려놓는 것
2. **예외처리 회피**
   - 예외처리를 자신이 담당하지 않고 자신을 호출한 쪽으로 던져버리는 것
   - 예외를 복구하는 것 처럼 **의도가 분명해야 한다.**
   - 자신을 사용하는 쪽에서 예외를 다루는 게 최선의 방법이라는 **분명한 확신이 있어야 한다.**
3. **예외 전환**
   - 예외를 복구해서 정상적인 상태로는 만들 수 없기 때문에 예외를 메소드 밖으로 던지는 것
   - 하지만 **예외처리 회피와 달리, 발생한 예외를 그대로 넘기는 게 아니라 `적절한 예외로 전환해서 던진다`는 특징이 있다.**
   
### 예외 전환의 두 가지 목적

**첫 번째**  
내부에서 발생한 예외를 그대로 던지는 것이 그 예외상황에 대한 적절한 의미를 부여해주지 못하는 경우, **의미를 분명하게 해줄 수 있는 예외로 바꿔주기 위해**  
보통 전환하는 예외에 원래 발생한 예외(`getCause()` , `initCause()`)를 담아서 **중첩 예외**로 만드는 것이 좋다.    

**두 번째**  
예외를 처리하기 쉽고 단순하게 만들기 위해 **포장**하는 것이다.  
중첩 예외를 이용해 새로운 예외를 만들고 **원인**이 되는 예외를 내부에 담아서 던지는 방식은 같다.  
하지만 의미를 명확하게 하려고 다른 예외로 전환하는 것이 아니다.    
주로 예외처리를 강제하는 **체크 예외를 언체크 예외인 런타임 예외로 바꾸는 경우에 사용한다.**  
어차피 **복구가 불가능한 예외라면 가능한 한 빨리 런타임 예외로 포장해 던지게 해서 다른 계층의 메소드를 작성할 때 불필요한 throws 선언이 들어가지 않도록 해줘야 한다.**

## 4.1.4 예외처리 전략

예외를 효과적으로 사용하고, 예외가 발생하는 코드를 깔끔하게 정리하는 데는 여러 가지 신경 써야 할 사항이 많다.  

### `add()` 메소드 예외처리

충분히 복구가 가능한 `DuplicateUserIdException` 런타임 예외
- 런타임 예외도 `throws`를 선언할 수 있다
대부분 복구가 불가능한 `SQLException` 체크 예외는 `throws`를 타고 앞으로 전달되게 하지말고 **런타임 예외로 전환하자**  

```java
public class DuplicateUserIdException extends RuntimeException {
    public DuplicateUserIdException(Throwable cause) {
        super(cause);
    }
}

...

public void add(final User user) throws DuplicateUserIdException {
     try { 
        // 사용자 저장 로직
        
     } catch (SQLException e) {
        if (e.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
            throw new DuplicateUserIdException(e.getCause());
         }
         throw new RuntimeException(e);
     }
}
```

## 4.1.5 SQLException은 어떻게 됐나?

스프링의 **예외처리 전략과 원칙**을 잘 알고 있어야 한다.  
먼저 생각해 볼 사항은 **99%의 `SQLException`은 복구가 불가능한 예외인 것**  
더군다나 DAO 밖에서 `SQLException`을 다룰 수 있는 가능성은 거의 없다.  

따라서 예외처리 전략을 적용해야 한다. 가능한 빨리 언체크/런타임 예외로 전환해줘야 한다.
스프링의 **JdbcTemplate**은 이 예외처리 전략을 따르고 있다.  
**JdbcTemplate 템플릿과 콜백안에서 발생하는 모든 SQLException을 런타임 예외인 `DataAccessException`으로 포장해서 던져주기 때문에 `throws SQLException`이 사라진 것이다.**  

# **4.2 예외 전환**

스프링의 **JdbcTemplate**이 던지는 `DataAccessException`은 일단 **런타임 예외로 `SQLException`을 포장**해주는 역할을 한다.  
또한 `SQLException`에 담긴 다루기 힘든 상세한 예외 정보를 의미 있고 일관성 있는 예외로 전환해서 추상화해주려는 용도로 쓰이기도 한다.  

## 4.2.1 JDBC의 한계

**JDBC**는 자바를 이용해 `DB에 접근하는 방법을 추상화된 API 형태로 정의`해놓고, 각 DB 업체가 JDBC 표준을 따라 만들어진 드라이버를 제공하게 해준다.  
내부 구현은 DB마다 다르겠지만 JDBC의 `Connection`, `Statement`, `ResultSet`등의 표준 인터페이스를 통해 기능을 제공하기 때문에 자바 개발자는 일관된 방법으로 개발할 수 있다.  

하지만 DB종류에 상관없이 사용할 수 있는 **데이터 엑세스 코드**를 작성하는 일은 쉽지 않다.  
현실적으로 DB를 자유롭게 바꾸어 사용할 수 있는 DB프로그램을 작성하는 데는 **두 가지 걸림돌**이 있다. 

1. **비표준 SQL**
   - 대부분의 DB는 표준을 따르지 않는 비표준 문법과 기능도 제공한다.
   - 비표준 문법과 기능이 담긴 SQL은 DAO에 종속되고 해당 DAO는 특정 DB에 종속되어 버리고 만다.
   - 결국 DB별로 별도의 DAO를 만들거나 SQL을 외부에 독립시켜서 DB에 따라 변경해 사용하는 방법이 있다.
2. **호환성 없는 SQLException의 DB 에러정보**
   - DB마다 SQL만 다른 것이 아니라 에러의 종류와 원인도 제각각이라는 점이다.
   - **그래서 JDBC는 데이터 처리 중에 발생하는 다양한 예외를 그냥 `SQLException`하나에 모두 담아버린다**
     - **JDBC API**는 `SQLException` 한 가지만 던지도록 설계되어 있다.
     - 예외가 발생한 원인은 `SQLException`안에 담긴 **에러 코드**와 **SQL 상태정보**를 참조해야 한다.
   - `getErrorCode()`로 가져올 수 있는 DB 에러코드도 DB별로 모두 다르다.
   - `getSQLState()`로 **SQL 상태정보**를 부가적으로 제공한다.
     - 이 상태정보는 DB별로 달라지는 에러 코드를 대신할 수 있도록 Open Group의 **XOPEN SQL**스펙에 정의된 SQL 상태 코드를 따르도록 정의 되어 있다.
     - JDBC3.0 = SQL99의 관례를 따르도록
     - JDBC4.0 = SQL2003의 관례를 따르도록
   - 따르도록 정의만 되어 있지 **SQL 상태정보**를 믿고 개발하는 것은 위험하다.

## 4.2.2 DB 에러 코드 매핑을 통한 전환 ⭐️

이번에는 `SQLException`의 비표준 에러코드와 SQL 상태정보에 대한 해결책을 알아보자.  
- DB 업체별로 만들어 유지해오고 있는 **DB 전용 에러 코드**가 더 정확한 정보이다.

해결방법은 **DB별 에러 코드를 참고해서 발생한 예외의 원인이 무엇인지 해석해 주는 기능을 만드는 것이다.**  
스프링은 `DataAccessException`의 서브클래스로 세분화된 예외 클래스들을 정의하고 있다.  
- [`Spring Docs` DataAccessException](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/dao/DataAccessException.html)

스프링은 **DB별 에러 코드를 분류해서 스프링이 정의한 예외 클래스와 매핑해놓은 `에러 코드 매핑정보 테이블`을 만들어두고 이를 이용한다.**
- `SQLErrorCodeSQLExceptionTranslator.doTranslate()`,`SQLExceptionSubclassTranslator.class`를 참고
**JdbcTemplate**은 `DataAccessException`을 포장하는 것이 아니라 `DataAccessException` 계층구조의 클래스 중 하나로 매핑해준다.  

## 4.2.3 DAO 인터페이스의 DataAccessException 계층 구조

`DataAccessException`은 **JDBC 외의 자바 데이터 엑세스 기술에서 발생하는 예외에도 적용된다.**  
자바에는 **JDBC**외에도 (JDBC와는 성격과 사용방법이 크게 다르지만) **JDO**, **JPA**존재한다.  
- *JDBC를 기반으로 하고, 성격도 비슷하지만 사용 방법과 API, 발생하는 예외가 다른 `iBatis`도 있다.*

여기서 중요한 것은 `DataAccessException`은 **의미가 같은 예외라면 데이터 엑세스 기술의 종류와 상관없이 일관된 예외가 발생하도록 만들어 준다는 것이다.**  
스프링이 왜 이렇게 `DataAccessException` 계층 구조를 이용해 기술에 독립적인 예외를 정리하고 사용하게 하는지 알아보자

### DAO 인터페이스와 구현의 분리

**데이터 엑세스 로직을 담은 코드를 성격이 다른 코드에서 분리**해놓기 위해 DAO를 따로 만들어 사용한다.  
DAO를 사용하는 쪽에서는 DAO가 내부를 신경쓰지 않아도 된다.  
하지만 **메소드 선언에 나타나는 예외 정보가 문제가 될 수 있다.**
- 인터페이스 메소드에도 영향이 간다.

**결국 인터페이스로 메소드의 구현은 추상화했지만 구현 기술마다 던지는 예외가 다르기 때문에 메소드의 선언이 달라진다는 문제가 발생한다.**
다행히도 JDBC 보다는 늦게 등장한 JDO, Hibernate, JPA 등의 기술들은 런타임 예외를 사용한다.  
따라서 `throws`에 선언을 해주지 않아도 된다.  

**하지만 다른 문제가 있다.**  
중복 키 에러처럼 비즈니스 로직에서 의미 있게 처리할 수 있는 예외도 있다.  
데이터 엑세스 기술이 달라지면 같은 상황에서도 다른 종류의 예외가 던져진다는 점이다.  

1. JPA는 `PersistenceException`
2. Hibernate는 `HibernateException`
3. JDO는 `JdoException`
4. JDBC는 `SQLException`

따라서 DAO를 사용하는 클라이언트 입장에서는 **DAO의 사용 기술에 따라서 예외 처리 방법이 달라져야한다.**  
단지 인터페이스로 추상화하고, 일부 기술에서 발생하는 체크 예외를 런타임 예외로 전화하는 것만으론 불충분하다.  

### 데이터 엑세스 예외 추상화와 DataAccessException 계층구조

그래서 스프링은 자바의 다양한 데이터 엑세스 기술을 사용할 때 발생하는 예외들을 추상화해서 `DataAccessException` 계층구조 안에 정리해 놓았다.  

예를 들어 JDO,JPA,하이버네이트처럼 오브젝트/엔티티 단위로 정보를 업데이트하는 경우에는 **낙관적인 락킹**이 발생할 수 있다.  
이 낙관적인 락킹은 같은 정보를 두 명 이상의 사용자가 동시에 조회하고 순차적으로 업데이트를 할 때, 뒤늦게 업데이트한 것이 먼저 업데이트 한것을 덮어쓰지 않도록 막아주는 데 쓸 수 있는 편리한 기능이다.  
이런 예외들은 사용자에게 적절한 안내 메시지를 보여주고, 다시 시도할 수 있도록 해줘야 한다.  
하지만 **역시 각각 다른 종류의 낙관적인 락킹 예외를 발생시킨다.**  
그런데 스프링의 예외 전환 방법을 적용하면 기술에 상관없이 `DataAccessException`의 서브 클래스인 `ObjectOptimisticLockingFailureException`으로 통일 시킬 수 있다.  

`DataAccessException`의 예외 사용법은 11장에서 더 자세하게 살펴보자

## 4.2.4 기술에 독립적인 UserDao만들기

1. 인터페이스 적용 [예제](https://github.com/jdalma/tobyspringin5/commit/d0f35cd7c6fdf66f52eca488d6ce45f5016be33e)
   - `UserDao`클래스를 인터페이스와 구현으로 분리해보자
2. 테스트 보완

```java
public class UserDaoTest {

   @Autowired
   private UserDaoJdbc dao; // 인스턴스를 UserDao로 바꿔줘야 할까?
   ...
}
```

중요한건 테스트의 관심이다.  
UserDao의 구현 기술에 상관없이 UserDao의 기능을 확인하는 것이라면 인터페이스로 받는 것이 낫다.  
반면에 특정 기술을 사용한 UserDao의 기능을 확인하는 것이라면 특정 타입으로 받는 것이 좋다.  
여기서의 관심은 **UserDao의 기능을 확인하는 것이니 `UerDao Interface`를 받도록 하자.**

이제 `UserDao`에서 중복된 키 관련 예외 테스트를 추가해보자.  

```java
@Test
void duplicateKey() {
   dao.deleteAll();
   
   dao.add(user1);
   assertThatThrownBy(() -> dao.add(user1))
          .isInstanceOf(DuplicateKeyException.class);
}
```

### DataAccessException 활용 시 주의사항

`DuplicateKeyException`은 JDBC를 이용하는 경우에만 발생한다.  
- 하이버네이트는 `ConstraintViolationException`을 발생시킨다.
- 스프링은 이를 해석해서 좀 더 포괄적 예외인 `DataIntegrityViolationException`으로 변환할 수 밖에 없다.

그 이유는 `SQLException`에 **DB의 에러 코드**를 바로 해석하는 JDBC와 달리 JPA, 하이버네이트, JDO 등에서는 **각 기술이 재정의한 예외를 가져와 스프링이 최종적으로 `DataAccessException`으로 변환하는데,**    
DB의 에러 코드와 달리 이런 예외들은 세분화 되어 있지 않기 때문이다.
**근본적인 한계 때문에 완벽하다고 기대할 수는 없다.**  

`DataAccessException`을 잡아서 처리하는 코드를 만들려고 한다면 미리 학습 테스트를 만들어서 실제로 전환되는 예외의 종류를 확인할 필요가 있다.  

스프링은 **`SQLException`을 `DataAccessException`으로 전환하는 다양한 방법을 제공한다.** [예제](https://github.com/jdalma/tobyspringin5/commit/165b4cca23fb2bd2ea927de1025bf5f1f258c478)
코드에서 직접 전환하고 싶다면 DB 에러 코드를 이용하여 `SQLExceptionTranslator`인터페이스를 구현한 클래스 중에서 `SQLErrorCodeSQLExceptionTranslator`를 사용하면 된다.  
이 `SQLErrorCodeSQLExceptionTranslator`는 에러 코드 변환에 필요한 DB의 종류를 알아내기 위해 현재 연결된 `DataSource`를 필요로 한다.  

> `SQLException`을 그대로 두거나 의미 없는 `RuntimeException`으로 뭉뚱그려서 던지는 대신 스프링의 `DataAccessException` 계층의 예외로 전환하게 할 수 있다.

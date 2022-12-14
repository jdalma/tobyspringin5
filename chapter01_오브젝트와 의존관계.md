# **1_오브젝트와 의존관계**

스프링이란 `어떻게 오브젝트가 설계되고 , 만들어지고 , 어떻게 관계를 맺고 사용되는지에 관심을 갖는 프레임워크`

## **1.1_초난감 DAO** [예제](https://github.com/jdalma/tobyspringin5/commit/0bc17c916abef092d5ecd2809b10eeb742b3b24b)

- **[자바 빈](https://jdalma.github.io/docs/algorithmTheory/dto/#javabean)**

## **1.2 DAO의 분리**

### 1.2.1 관심사의 분리

오브젝트에 대한 설계와 이를 구현한 코드 중 변하는 것과 변하지 않는 것이 있다.
끝이란 개념은 없다.
**미래의 변화를 어떻게 대비할 것인가?**
**어떻게 변경이 일어날 때 필요한 작업을 최소화하고 , 그 변경이 다른 곳에 문제를 일으키지 않게 할 수 있었을까?**
  - `분리 와 확장`을 고려한 설계를 해야한다
변화는 대체로 집중된 한 가지 관심에 대해 일어나지만 **그에 따른 작업은 한 곳에 집중되지 않는 경우가 많다는 점**이다
  - 변화가 한 번에 한가지 관심에 집중돼서 일어난다면, **우리가 준비해야 할 일은 한 가지 관심이 한 군데에 집중되게 하는 것**
  - 즉, **관심사의 분리** `관심이 같은 것끼리는 모으고 , 관심이 다른 것은 따로 떨어져 있게 하는 것` 


### 1.2.2 커넥션 만들기의 추출

- 현재 [UserDAO](https://github.com/jdalma/tobyspringin5/commit/0bc17c916abef092d5ecd2809b10eeb742b3b24b)는 변경이 일어날 때 엄청난 고통을 일으키는 원인이 된다.

1. **UserDAO**의 관심 사항
   1. DB와 연결을 위한 커넥션을 가져오는 것
   2. SQL 문장을 담을 `Statement`를 만들고 실행하는 것
   3. 사용한 리소스 `Statement` , `Connection` 오브젝트를 닫는 것
2. **중복 코드의 메서드 추출**
3. **변경사항에 대한 검증 : 리팩토링과 테스트**

### 1.2.3 DB커넥션 만들기의 독립

1. **상속을 통한 `getConnection()` 확장** [예제](https://github.com/jdalma/tobyspringin5/commit/e18bbe9faf50c6003a5be11020f6bef6c254af0b)
   - 기존 `UserDAO`를 추상 클래스로 정의하고 `getConnection` 메서드를 추상 메서드로 정의한다.
   - 그리고 추가적인 DAO (`N` , `D`)는 추상 클래스인 `UserDao`의 추상 메서드인 `getConnection`을 재정의한다.

<br>

**UserDao** : **어떻게 데이터를 등록하고 가져올 것인가**라는 관심을 담당<br>
**N , DuserDao** : **DB 연결 방법은 어떻게 할것인가**라는 관심을 담당<br>
계층 구조를 통해 두 개의 관심이 독립적으로 분리되었다.<br>
서브클래스에서 변화가 필요한 부분을 바꿔서 쓸 수 있게 만든 이유는 바로 변화의 성격이 다른 것을 분리해서 , 서로 영향을 주지 않은채로 **각각 필요한 시점에 독집적으로 변경할 수 있게 하기 위해**서다.<br>
하지만 이 방법은 **상속을 사용했다는 단점**이 있다.<br>

1. 만약 이미 **UserDao**가 다른 목적을 위해 상속을 사용하고 있다면 어쩔 것인가?
2. 상속을 통한 상하위 클래스의 관계는 생각보다 밀접하다는 점 `상속관계는 긴밀한 결합을 허용한다`

> 이렇게 슈퍼클래스에 기본적인 로직의 흐름을 만들고 , 그 기능의 일부를 추상 메소드나 오버라이딩이 가능한 `protected` 메소드 등으로 만든 뒤 `서브클래스에서 이런 메소드를 필요에 맞게 구현해서 사용하도록 하는 방법`을 **템플릿 메소드 패턴**이라고 한다.
>
> 그리고 `서브 클래스에서 구체적인 오브젝트 생성 방법을 결정하게 하는 것`을 **팩토리 메소드 패턴**이라고 한다.

## **1.3 DAO의 확장**

- **UserDao**는
  1. JDBC API를 사용할 것인가 , DB 전용 API를 사용할 것인가
  2. 어떤 테이블 이름과 필드 이름을 사용해 어떤 SQL을 만들 것인가
  3. 어떤 오브젝트를 통해 DB에 저장할 정보를 전달받고 , DB에서 꺼내온 정보를 저장해서 넘겨줄 것인가

### 1.3.1 클래스의 분리
관심사를 분리하는 작업을 점진적으로 진행해왔다.

이번에는 **완전히 독립적인 클래스로 만들어 보자**
**SimpleConnectionMaker** 클래스를 추가하고 `makeNewConnection()`메소드를 작성했지만 아래와 같은 문제가 있다.
  1. `makeNewConnection()`을 사용해 DB 커넥션을 가져오게 했는데 다른 D사 에서 개발한 커넥션 제공 클래스는 `openConneciton()`이라면??
  2. DB커넥션을 제공하는 클래스가 어떤 것인지를 UserDao는 구체적으로 알고있어야 하는 것

### 1.3.2 인터페이스의 도입
가장 좋은 해결책은 **두 개의 클래스가 서로 긴밀하게 연결되어 있지 않도록 중간에 추상적인 느슨한 연결고리를 만들어 주는 것**
  - 추상화 : 어떤 것들의 공통적인 성격을 뽑아내어 이를 따로 분리해내는 작업

인터페이스를 사용하게 한다면 인터페이스의 메소드를 통해 할 수 있는 기능에만 관심을 가지면 된다.<br>
**그 기능을 어떻게 구현했는지에는 관심을 둘 필요가 없다.**

### 1.3.3 관계설정 책임의 분리
UserDao의 클라이언트 오브젝트가 바로 제 3의 관심사항인 **UserDao와 ConnectionMaker 구현 클래스의 관계를 결정해주는 기능**을 분리해서 두기에 적절하다.
**UserDao의 클라이언트**에서 UserDao가 **어떤 ConnectionMaker의 구현 클래스**를 사용할지를 결정하도록 만들어주자.
**오브젝트 사이의 관계는 런타임 시에 한쪽이 다른 오브젝트의 레퍼런스를 갖고 있는 방식으로 만들어진다.**

```
"다형성"
클래스 사이의 관계는 코드에 다른 클래스 이름이 나타나기 때문에 만들어지는 것이다.
하지만 오브젝트 사이의 관계는 그렇지 않다!
```

### 1.3.4 원칙과 패턴 ⭐️

1. **개방 페쇄 원칙 `OCP``**
- 클래스나 모듈은 확장에는 열려 있어야 하고 변경에는 닫혀 있어야 한다.

2. **높은 응집도와 낮은 결합도**
- 개방 폐쇄 원칙은 높은 응집도와 낮은 결합도라는 소프트웨어 개발의 고전적인 원리로도 설명이 가능하다.
- **응집도가 높다는 건** 하나의 모듈 , 클래스가 하나의 책임 또는 관심사에만 집중되어 있다는 뜻
- 하나의 공통 관심사는 한 클래스에 모여있다.
   - 높은 응집도는 클래스 레벨뿐 아니라 , 패키지 , 컴포넌트 , 모듈에 이르기까지 그 대상의 크기가 달라도 동일한 원리로 적용될 수 있다.
- **낮은 결합도는** 책임과 관심사가 다른 오브젝트 또는 모듈과 느슨하게 연결된 형태를 유지하여 서로 독립적이고 알 필요 없게 만드는 것

3. **전략 패턴**
- 개선한 `UserService-UserDao-ConnectionMaker`구조를 **전략 패턴**에 해당한다고 볼 수 있다.
- 자신의 기능 맥락에서 , 필요에 따라 변경이 필요한 알고리즘을 인터페이스를 통해 통째로 외부로 분리시키고 , 이를 구현한 구체적인 알고리즘 클래스를 필에요 따라 바꿔서 사용할 수 있게 하는 디자인 패턴이다.

```
UserDao는 전략 패턴의 컨텍스트에 해당한다.
컨텍스트는 자신의 기능을 수행하는데 필요한 기능 중에서 변경 가능한 ,
DB 연결 방식이라는 알고리즘을 ConnectionMaker라는 인터페이스로 정의히고 ,
이를 구현한 클래스 , 즉 전략을 바꿔가면서 사용할 수 있게 분리했다.
```

## **1.4 제어의 역전(IoC)**

### 1.4.1 오브젝트 팩토리

UserDao와 ConnectionMaker 구현 클래스의 오브젝트를 만드는 것
위 **두 개의 오브젝트가 연결돼서 사용할 수 있도록 관계를 맺어주자**

- **팩토리**
  - 객체 생성 방법을 결정하고 그렇게 만들어진 오브젝트를 돌려주는 것인데 , 이런 일을 하는 오브젝트를 흔히 **팩토리**라고 부른다.
  - **오브젝트를 생성하는 쪽**과 **생성된 오브젝트를 사용하는 쪽**의 `역할과 책임을 깔끔하게 분리하려는 목적`으로 사용된다.

```java
// 생성
public class DaoFactory {
    public UserDao userDao(){
        ConnectionMaker maker = new DConnectionMaker();
        return new UserDao(maker);
    }
}

// 사용
UserDao dao = new DaoFactory().userDao();
```

<br>

- **설계도로서의 팩토리**
  - 실질적인 컴포넌트 : UserDao , ConnectionMaker
  - 애플리케이션을 구성하는 컴포넌트의 구조와 관계를 정의한 설계도 : DaoFactory

### 1.4.2 오브젝트 팩토리의 활용
현재 오브젝트 팩토리에 **추가적인 DAO를 생성하는 책임도 맡게 된다면 어떤 ConnectionMaker 구현 클래스를 사용할지를 결정하는 기능이 중복되게 나타날 것이다.**
ConnectionMaker의 구현 클래스를 결정하고 오브젝트를 만드는 코드를 별도의 메소드로 뽑아내자.

```java
public class DaoFactory {
    public UserDao userDao(){
        return new UserDao(connectionMaker());
    }
    
    public MeesageDao messageDao(){
        return new MeesageDao(connectionMaker());
    }
    
    public ConnectionMaker connectionMaker(){
        return new DConnectionMaker();
    }
}
```

### 1.4.3 제어권의 이전을 통한 제어관계 역전
**프로그램의 제어 흐름 구조가 뒤바뀌는 것**
일반적으로는
1. 클래스의 오브젝트를 직접 생성하고 , 만들어진 오브젝트의 메소드를 사용한다.
2. 자신이 사용할 구현 클래스를 자신이 결정하고,
3. 오브젝트를 필요한 시점에서 생성해두고 , 각 메소드에서 이를 사용한다.

<br>

**모든 오브젝트가 자신이 사용할 클래스를 결정하고 , 언제 어떻게 그 오브젝트를 만들지를 스스로 관장한다.**<br>
**모든 종류의 작업을 사용하는 쪽에서 제어하는 구조다.**<br>
제어의 역전이란 `이런 제어 흐름의 개념을 거꾸로 뒤집는 것`이다.<br>
제어의 역전에서는 프레임워크 또는 컨테이너와 같이 애플리케이션 컴포넌트의 생성과 관계 설정 , 사용 , 생명주기 관리 등을 관장하는 존재가 필요하다!


## **1.5 스프링의 IoC** ⭐️
스프링의 핵심을 담당하는 건 , 바로 **빈 팩토리**또는 **어플리케이션 컨텍스트**라고 불리는 것이다.

### 1.5.1 오브젝트 팩토리를 이용한 스프링 IoC

> **Bean**
> - 스프링이 제어권을 가지고 직접 만들고 관계를 부여하는 오브젝트
> - **스프링 컨테이너가 생성과 관계설정 , 사용 등을 제어해주는 제어의 역전이 적용된 오브젝트**
> - `자바 빈` 또는 `엔터프라이즈 자바 빈` 에서 말하는 빈과 비슷한 오브젝트 단위의 애플리케이션 컴포넌트를 말한다.

**어플리케이션 컨텍스트와 설정정보**<br>
빈의 생성과 관계설정 같은 제어를 담당하는 IoC 오브젝트를 **빈 팩토리**라고 부른다.<br>
*보통 빈 팩토리 보다는 이를 조금 더 확장한 **어플리케이션 컨텍스트***를 주로 사용한다.<br>
이 책에서는 두 가지가 동일하다고 생각하면 된다.<br>

> **어플리케이션 컨텍스트** 그 자체로는 어플리케이션 로직을 담당하지는 않지만 IoC 방식을 이용해 애플리케이션 컴포넌트를 생성하고 , 사용할 관계를 맺어주는 등의 책임을 담당하는 것이다. 

**DaoFactory를 사용하는 애플리케이션 컨텍스트**<br>

```kotlin
implementation("org.springframework:spring-context:5.3.22")
```

1. `@Configuration`
   - 오브젝트 설정을 담당하는 클래스라고 인식할 수 있도록
   - **애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시**
2. `@Bean`
   - **오브젝트 생성을 담당하는 IoC용 메소드라는 표시**

<br>

`@Configuration`이 붙은 자바 코드를 설정정보로 사용하려면 아래와 같이 사용할 수 있다.

```java
public static void main(String[] args) {
    ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
    UserDao dao = context.getBean("userDao" , UserDao.class);
    System.out.println(dao);

    ConnectionMaker connectionMaker = context.getBean("connectionMaker" , ConnectionMaker.class);
    System.out.println(connectionMaker);

//        springbook.chapter01.UserDao@7c0c77c7
//        springbook.chapter01.DConnectionMaker@7adda9cc
}
```

`getBean({param1} , {param2})`은 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드다.

- `{param1}` : **등록된 빈의 이름**
- `{param2}` : 자바 5 이상의 제네릭 메소드 방식을 사용한 **리턴타입** 지정

### 1.5.2 애플리케이션 컨텍스트의 동작방식
애플리케이션 컨텍스트를 **빈 팩토리** , **IoC 컨테이너** 또는 간단히 **스프링 컨테이너**라고 부른다.<br>
**DaoFactory**가 UserDao를 비롯한 DAO 오브젝트를 생성하고 DB 생성 오브젝트와 관계를 맺어주는 제한적인 역할 (`애플리케이션 컨텍스트가 활용하는 IoC 설정정보`) 을 하는 데 반해,
**애플리케이션 컨텍스트**는 애플리케이션에서 `IoC를 적용해서 관리할 모든 오브젝트에 대한 생성과 관계설정을 담당`한다.<br>

<br>

```
애플리케이션 컨텍스트는 DaoFactory클래스를 설정정보로 등록해두고 @Bean이 붙은 메소드의 이름을 가져와 빈 목록을 만들어둔다.
```

DaoFactory를 오브젝트 팩토리로 직접 사용했을 때와 비교해서 **애플리케이션 컨텍스트를 사용했을 때 얻을 수 있는 장점은 아래와 같다.**<br>

1. 클라이언트의 구체적인 팩토리 클래스를 알 필요가 없다.
2. 애플리케이션 컨텍스트는 종합 IoC 서비스를 제공해준다.
3. 애플리케이션 컨텍스트는 빈을 검색하는 다양한 방법을 제공한다.

### 1.5.3 스프링 IoC의 용어 정리

- **빈 `Bean`**
  - 스프링이 IoC 방식으로 관리 , 직접 생성과 제어를 담당하는 오브젝트
  - *스프링을 사용하는 애플리케이션에서 만들어지는 모든 오브젝트가 다 빈은 아니다*
- **빈 팩토리 `Bean Factory`**
  - IoC를 담당하는 핵심 컨테이너
- **애플리케이션 컨텍스트 `Application Context`**
  - 빈 팩토리를 확장한 IoC 컨테이너
  - 스프링이 제공하는 각종 부가 서비스를 추가로 제공한다.
- **설정정보/설정 메타정보 `Configuration Metadata`**
  - 애플리케이션 컨텍스트 또는 빈 팩토리가 IoC를 적용하기 위해 사용되는 메타정보를 말한다.
  - 애플리케이션의 형상정보라고도 불린다.
- **컨테이너 또는 IoC컨테이너**
  - IoC 방식으로 빈을 관리한다는 의미에서 애플리케이션 컨텍스트나 빈 팩토리를 칭하는 것과 같다.

## **1.6 싱글톤 레지스트리와 오브젝트 스코프**

**오브젝트의 동일성과 동등성**<br>
- **동일성** : `==` 두 개의 오브젝트가 완전히 같은 것
- **동등성** : `equals` 동일한 정보를 담고 있는 것

<br>

**스프링은 여러 번에 걸쳐 빈을 요청하더라도 매번 동일한 오브젝트를 돌려준다.**

### 1.6.1 싱글톤 레지스트리로서의 애플리케이션 컨텍스트
애플리케이션 컨텍스트는 IoC 컨테이너 이며, 동시에 싱글톤을 저장하고 관리하는 **싱글톤 레지스트리**이기도 하다.<br>
별 다른 설정을 하지 않으면 내부 오브젝트를 모두 싱글톤으로 만든다.<br>
- *디자인 패턴에서 나오는 싱글톤 패턴과 비슷한 개념이지만 그 구현 방법은 확연히 다르다.*

<br>

서블릿은 자바 엔터프라이즈 기술의 가장 기본이 되는 **서비스 오브젝트**라고 할 수 있다.<br>
서블릿은 대부분 **멀티스레드 환경에서 싱글톤으로 동작**한다.<br>
서버환경에서는 자바의 기본적인 싱글톤 패턴으로 구현하면 하나만 만들어지는 것을 보장하지 못한다.<br>
- 서버에서 클래스 로더를 어떻게 구성하고 있느냐에 따라서 하나 이상의 오브젝트가 만들어 질 수 있다.
- 따라서 서버환경에서는 싱글톤이 꼭 보장된다고 볼 수 없다.
- 여러 개의 JVM에 분산돼서 설치가 되는 경우에도 각각 독립적으로 오브젝트가 생기기 때문에 싱글톤으로서의 가치가 떨어진다.

<br>

스프링은 `직접 싱글톤 형태의 오브젝트를 만들고 관리`하는 **싱글톤 레지스트리**를 제공한다.<br>
스프링 컨테이너를 사용해서 **생성과 관계설정 , 사용 등에 대한 제어권을 컨테이너에게 넘기면 손쉽게 싱글톤 방식으로 만들어져 관리**되게 할 수 있다.<br>

## **1.7 의존관계 주입(`DI`)**

### 1.7.1 제어의 역전과 의존관계 주입
**IoC**는 폭넓게 사용되는 용어이기 때문에 **의존관계 주입**이라는 의도가 명확히 드러나는 이름을 사용하기 시작했다.<br>
초기에는 주로 IoC 컨테이너라고 불리던 스프링이 지금은 **DI 컨테이너**라고 더 많이 불린다.


### 1.7.2 런타임 의존관계 설정
**의존관계**
- 두 개의 클래스 또는 모듈이 의존관계에 있다고 말할 때는 항상 방향성을 부여해줘야한다.
- 즉, `누가 누구에게 의존하는 관계에 있다는 식`
- A에서 B에 정의된 메소드를 호출해서 사용하는 경우 **사용에 대한 의존관계**
  - B가 수정되면 A에 영향을 준다. A가 수정되면 B에 영향을 주지 않는다.
- **인터페이스에 대해서만 의존관계를 만들어두면 인터페이스 구현 클래스와의 관계는 느슨해지면서 변화에 영향을 덜 받는 상태가 된다.**
  - 클래스 모델이나 코드에는 런타임 시점의 의존관계가 드러나지 않는다.

<br>

**런타임 의존관계** 또는 **오브젝트 의존관계**
- 런타임 시에 오브젝트 사이에서 만들어지는 의존관계
  - 즉, **실제 사용 대상인 오브젝트를 `의존 오브젝트`라고 한다.**
- 설계시점의 의존관계가 실체화 된 것
- 인터페이스를 통해 설계 시점에 느슨한 의존관계를 갖는 경우에는 UserDao의 오브젝트가 런타임 시에 사용할 오브젝트가 어떤 클래스로 만든 것인지 미리 알 수가 없다.

<br>

**의존관계 주입**
- **구체적인 의존 오브젝트와 그것을 사용할 주체, 보통 클라이언트라고 부르는 오브젝트를 런타임 시에 연결해주는 작업을 말한다.**
- 런타임 시점의 의존관계는 **제 3의 존재**가 결정한다.
  - 제 3의 존재 : 애플리케이션 컨텍스트 , 빈 팩토리 , IoC 컨테이너 등

### 1.7.3 의존관계 검색과 주입
**의존관계 검색**과 **의존관계 주입**을 적용할 때 발견할 수 있는 중요한 차이점이 하나 있다.
- 의존관계 검색 방식에서는 **검색하는 오브젝트는 자신이 스프링의 빈일 필요가 없다는 것**

### 1.7.4 의존관계 주입의 응용
**UserDao** → **CountConnectionMaker** → **ConnectionMaker**

### 1.7.5 메소드를 이용한 의존관계 주입
1. 생성자를 이용한 주입
2. 수정자 메소드를 이용한 주입

```java
public class UserDao {

    private ConnectionMaker connectionMaker;

    public void setConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }
    ...
}

@Configuration
public class DaoFactory {

    @Bean
    public UserDao userDao() {
        UserDao dao = new UserDao();
        dao.setConnectionMaker(connectionMaker());
        return dao;
    }
    ...
}
```

3. 일반 메소드를 이용한 주입
   - 한 번에 필요한 모든 파라미터를 다 받아야하는 생성자보다 낫다.

## **1.8 XML을 이용한 설정**
1. 단순한 텍스트 파일이기 때문에 다루기 쉽다.
2. 쉽게 이해할 수 있다.
3. 컴파일과 같은 별도의 빌드 작업이 없다.

### 1.8.1 XML설정

|        | 자바 코드 설정정보              | XML 설정정보             |
|--------|-------------------------|----------------------|
| 빈 설정파일 | @Configuration          | <beans>              |
| 빈의 이름  | @Bean methodName()      | <bean id="methodName"> |
| 빈의 클래스 | return new BeanClass(); | class="BeanClass"    |

```xml
<beans>
    <bean id="connectionMaker" class="springbook.user.daoDConnectionMaker"/>
    
    <bean id="userDao" class="springbook.dao.UserDao">
        <property name="connectionMaker" ref="connectionMaker"/>
    </bean>
</beans>

```

### 1.8.2 XML을 이용하는 애플리케이션 컨텍스트
XML에서 빈의 의존관계 정보를 이용한 IoC/DI 작업에는 **GenericXmlApplicationContext**를 사용한다.<br>



### 1.8.3 DataSource 인터페이스로 변환
**DataSource 인터페이스 적용**
- 자바에서는 DB 커넥션을 가져오는 오브젝트의 기능을 추상화해서 비슷한 용도로 사용할 수 있게 만들어진 **DataSource 인터페이스**가 존재한다.
- 이미 다양한 방법으로 **DB 연결**과 **폴링**기능을 갖춘 많은 DataSource 구현 클래스가 존재한다.
- DB의 종류나 아이디 , 비밀번호 정도는 지정할 수 있는 방법을 제공한다.

<br>

**자바 코드 설정 방식** [예제](https://github.com/jdalma/tobyspringin5/commit/98a683949ba1c9f50d5557a39937f7787da07205)

```java
@Configuration
public class DaoFactory {

    private final String URL = "jdbc:mysql://localhost/springbook?autoReconnect=true&useSSL=false&serverTimezone=UTC";
    private final String ID = "springbook";
    private final String PASSWORD = "springbook!@";

    @Bean
    public UserDao userDao(){
        UserDao dao = new UserDao();
        dao.setDataSource(dataSource());
        return dao;
    }
    
    @Bean
    public DataSource dataSource(){
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl(URL);
        dataSource.setUsername(ID);
        dataSource.setPassword(PASSWORD);

        return dataSource;
    }
}
```

### 1.8.4 프로퍼티 값의 주입

```xml
<property name="driverClass" value="com.mysql.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://localhost/springbook?autoReconnect=true&useSSL=false&serverTimezone=UTC"/>
<property name="username" value="springbook"/>
<property name="password" value="springbook!@"/>
```

- `com.mysql.jdbc.Driver` 이렇게 문자열을 주는데 어떻게 속성을 변경할 수 있을까?

```java
Class driverClass = Class.forName("com.mysql.jdbc.Driver");
dataSource.setDriver(driverClass);
```

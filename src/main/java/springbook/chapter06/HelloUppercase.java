package springbook.chapter06;

import java.util.Locale;

// 위임과 기능부가라는 두 가지 프록시의 기능을 모두 처리하는 전형적인 프록시 클래스
public class HelloUppercase implements Hello {

    // 위임할 타깃 오브젝트
    // 여기서는 타깃 클래스의 오브젝트인 것은 알지만, 다른 프록시를 추가할 수도 있으므로 인터페이스로 접근한다.
    Hello hello;

    public HelloUppercase(Hello hello) {
        this.hello = hello;
    }

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase(Locale.ROOT);
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase(Locale.ROOT);
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase(Locale.ROOT);
    }
}

package springbook.chapter06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void upgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() == null) {
                continue;
            }

            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel()).isEqualTo(level.nextLevel());
        }
    }

    @Test
    void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if (level.nextLevel() != null) {
                continue;
            }
            user.setLevel(level);
            assertThatThrownBy(() -> user.upgradeLevel())
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void simpleProxy() {
        final String name = "DALMA";
        Hello hello = new HelloTarget();
        assertThat(hello.sayHello(name)).isEqualTo("Hello " + name);
        assertThat(hello.sayHi(name)).isEqualTo("Hi " + name);
        assertThat(hello.sayThankYou(name)).isEqualTo("Thank You " + name);

        Hello proxiedHello = new HelloUppercase(hello);
        assertThat(proxiedHello.sayHello(name)).isEqualTo("HELLO " + name);
        assertThat(proxiedHello.sayHi(name)).isEqualTo("HI " + name);
        assertThat(proxiedHello.sayThankYou(name)).isEqualTo("THANK YOU " + name);

        // 생성된 다이내믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로 Hello 타입으로 캐스팅해도 안전하다.
        Hello dynamicProxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),    // 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
                new Class[] {Hello.class},      // 구현할 인터페이스
                new UppercaseHandler(new HelloTarget()));   // 부가기능과 위임 코드를 담은 InvocationHandler

        assertThat(dynamicProxiedHello.sayHello(name)).isEqualTo("HELLO " + name);
        assertThat(dynamicProxiedHello.sayHi(name)).isEqualTo("HI " + name);
        assertThat(dynamicProxiedHello.sayThankYou(name)).isEqualTo("THANK YOU " + name);
    }
}

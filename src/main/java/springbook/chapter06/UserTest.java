package springbook.chapter06;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }
}

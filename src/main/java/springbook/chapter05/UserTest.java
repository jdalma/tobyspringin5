package springbook.chapter05;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
}

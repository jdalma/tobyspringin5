package springbook.chapter06.pointcut;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

class TargetTest {

    @Test
    void methodSignaturePointcut() throws NoSuchMethodException {
        StringBuilder execution = new StringBuilder();
        execution.append("execution(")
                .append("public int ")
                .append("springbook.chapter06.pointcut.Target.minus(int,int) ")
                .append("throws java.lang.RuntimeException")
                .append(")");

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(execution.toString());

        Class<Target> target = Target.class;
        assertThat(pointcut.getClassFilter().matches(target)).isTrue();

        // Target.minus()
        Method minus = Target.class.getMethod("minus", int.class, int.class);
        assertThat(pointcut.getMethodMatcher().matches(minus, target)).isTrue();

        // Target.plus()
        Method plus = Target.class.getMethod("plus", int.class, int.class);
        assertThat(pointcut.getMethodMatcher().matches(plus, target)).isFalse();

        // Target.method()
        Method method = Target.class.getMethod("method");
        assertThat(pointcut.getMethodMatcher().matches(method, target)).isFalse();
    }
}

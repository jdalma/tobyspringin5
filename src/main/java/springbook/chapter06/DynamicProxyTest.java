package springbook.chapter06;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

import java.lang.reflect.Proxy;
import java.util.Locale;

public class DynamicProxyTest {

    @Test
    void simpleProxy() {
        // JDK 다이나믹 프록시
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );

        Assertions.assertThat(proxiedHello.sayHello("test")).isEqualTo("HELLO TEST");
    }

    @Test
    void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        // 타깃 설정
        pfBean.setTarget(new HelloTarget());
        // 부가기능을 담은 어드바이스 추가, 여러 개 추가 가능
        pfBean.addAdvice(new UppercaseAdvice());

        // FactoryBean이므로 getObject()로 생성된 프록시를 가져온다
        Hello proxiedHello = (Hello) pfBean.getObject();
        Assertions.assertThat(proxiedHello.sayHello("test")).isEqualTo("HELLO TEST");
    }

    static class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            // 리플렉션의 Method와 달리 메소드 실행시 "타깃 오브젝트"를 전달할 필요가 없다
            // MethodInvacation은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다
            String ret = (String) invocation.proceed();
            return ret.toUpperCase(Locale.ROOT);
        }
    }
}

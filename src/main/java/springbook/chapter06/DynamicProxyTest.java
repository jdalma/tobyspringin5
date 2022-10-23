package springbook.chapter06;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

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

    @Test
    void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        // 메소드 이름을 비교해서 대상을 선정하는 알고리즘을 제공하는 포인트컷
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        // sayH로 시작하는 모든 메소드
        pointcut.setMappedName("sayH*");

        // 포인트컷과 어드바이스를 Advisor로 묶어서 한 번에 추가
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assertions.assertThat(proxiedHello.sayHi("test")).isEqualTo("HI TEST");
        Assertions.assertThat(proxiedHello.sayHello("test")).isEqualTo("HELLO TEST");
        Assertions.assertThat(proxiedHello.sayThankYou("test")).isEqualTo("Thank You test");
    }
}

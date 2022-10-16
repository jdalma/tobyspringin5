package springbook.chapter06;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

public class UppercaseHandler implements InvocationHandler {
    Object target;

    // 다이내믹 프록시로부터 전달받은 요청을 다시 타깃 오브젝트에게 위임해야 하기 때문에
    // 타깃 오브젝트를 주입받아 둔다.
    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 타깃으로 위임, 인터페이스의 메소드 호출에 모두 적용된다.
        // 반환 객체가 문자열인 경우에만 대문자로 변경하고, 나머지는 그대로 반환환다.
        Object ret = method.invoke(target, args);
        if (ret instanceof String) {
            return ((String) ret).toUpperCase();
        }
        return ret;
    }
}

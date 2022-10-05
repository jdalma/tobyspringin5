package springbook.chapter03.templateCallbackExample;

public interface LineCallback<T> {

    T doSomethingWithLine(String line, T value);
}

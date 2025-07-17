package hello.springbook.user.learningtest.template;

public interface LineCallback<T> {
    T dosomethingWithLine(String line, T value);
}

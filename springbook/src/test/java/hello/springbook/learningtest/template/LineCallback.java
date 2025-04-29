package hello.springbook.learningtest.template;

public interface LineCallback<T> {
    T dosomethingWithLine(String line, T value);
}

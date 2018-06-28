package codehumane.common;

public interface Identifiable<T> {

    void setId(T id);

    T getId();
}

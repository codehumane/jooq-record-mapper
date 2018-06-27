package codehumane.jooq;

public interface Identifiable<T> {

    void setId(T id);

    T getId();
}

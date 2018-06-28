package codehumane.common;

import java.util.Arrays;

public interface Encodable<T> {

    static <T, E extends Enum<E> & Encodable<T>> E codeOf(E[] encodables, T code) {

        return Arrays
                .stream(encodables)
                .filter(c -> c.getCode().equals(code)).findFirst()
                .orElse(null);
    }

    T getCode();
}
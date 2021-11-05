package de.dercompiler.util;

import java.util.Objects;

public class Optional<E> {

    E value;

    public Optional() {
        this(null);
    }

    public Optional(E value) {
        this.value = value;
    }

    boolean hasValue() {
        return !Objects.isNull(value);
    }

    public E getValue() {
        return value;
    }
}

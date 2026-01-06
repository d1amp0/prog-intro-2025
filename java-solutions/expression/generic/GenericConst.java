package expression.generic;

import java.util.Objects;

public class GenericConst<T extends Number> implements GenericTripleExpression<T> {
    private final T value;

    public GenericConst(T value) {
        this.value = value;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return this.value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GenericConst<?> con)) return false;
        return value.equals(con.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, getClass());
    }
}
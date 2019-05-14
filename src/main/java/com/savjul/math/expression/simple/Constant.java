package com.savjul.math.expression.simple;

import com.savjul.math.expression.AbstractBaseExpression;

import java.util.Objects;

public class Constant<T extends Number> extends AbstractBaseExpression {
    public static final Constant<Integer> MINUS_ONE = Constant.of(-1);
    public static final Constant<Integer> ZERO = Constant.of(0);
    public static final Constant<Integer> ONE = Constant.of(1);
    public static final Constant<Double> DOUBLE_ZERO = Constant.of(0.0);
    public static final Constant<Double> DOUBLE_ONE = Constant.of(1.0);
    private final T value;

    protected Constant(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static <V extends Number> Constant<V> of(V v) {
        return new Constant<>(v);
    }

    public T getValue() { return value; }

    public boolean isSameType(Constant<?> other) {
        return this.value.getClass() == other.value.getClass();
    }

    public boolean isSameType(Class<? extends Number> klass) {
        return klass == value.getClass();
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constant<?> that = (Constant<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

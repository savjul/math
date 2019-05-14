package com.savjul.math.expression.simple;

import com.savjul.math.expression.AbstractBaseExpression;

import java.util.Objects;

public class Constant<T extends Number> extends AbstractBaseExpression {
    public static final IntegerConstant MINUS_ONE = IntegerConstant.of(-1);
    public static final IntegerConstant ZERO = IntegerConstant.of(0);
    public static final IntegerConstant ONE = IntegerConstant.of(1);
    public static final DoubleConstant DOUBLE_ZERO = DoubleConstant.of(0.0);
    public static final DoubleConstant DOUBLE_ONE = DoubleConstant.of(1.0);
    private final T value;

    protected Constant(T value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static <V extends Number> Constant<V> of(V v) {
        return new Constant<>(v);
    }

    public T getValue() { return value; };

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

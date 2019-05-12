package com.savjul.math.expression.simple;

import java.util.Objects;

public final class IntegerConstant extends NumericConstant {
    public static final IntegerConstant MINUS_ONE = IntegerConstant.of(-1);
    public static final IntegerConstant ZERO = IntegerConstant.of(0);
    public static final IntegerConstant ONE = IntegerConstant.of(1);
    private final Long value;

    private IntegerConstant(Long value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static IntegerConstant of(Long value) {
        return new IntegerConstant(value);
    }

    public static IntegerConstant of(Integer value) {
        return new IntegerConstant(value.longValue());
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerConstant that = (IntegerConstant) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

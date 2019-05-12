package com.savjul.math.expression.simple;

import java.util.Objects;

public final class DoubleConstant extends NumericConstant {
    public static final DoubleConstant ZERO = new DoubleConstant(0.0);
    public static final DoubleConstant ONE = new DoubleConstant(1.0);

    private final Double value;

    public static DoubleConstant of(Double value) {
        return new DoubleConstant(value);
    }

    private DoubleConstant(Double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleConstant that = (DoubleConstant) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

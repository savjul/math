package com.savjul.math.expression;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DoubleConstant extends AbstractBaseExpression {
    public static final DoubleConstant ZERO = new DoubleConstant(null, 0.0);
    public static final DoubleConstant ONE = new DoubleConstant(null, 1.0);

    private final Double value;

    public static Expression of(Double value) {
        return new DoubleConstant(null, value);
    }

    private DoubleConstant(Expression parent, Double value) {
        super(parent);
        this.value = value;
    }

    @Override
    public Expression withParent(Expression parent) {
        return new DoubleConstant(parent, value);
    }

    @Override
    public boolean isConstant() {
        return true;
    }


    public Double getValue() {
        return value;
    }

    @Override
    public String render() {
        return this.value.toString();
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

    @Override
    public int order() {
        return this.getParent() instanceof Term ? ExpressionConstants.DOUBLE_ORDER_TERM
                : ExpressionConstants.DOUBLE_ORDER_OTHER;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof DoubleConstant) {
            return this.value.compareTo(((DoubleConstant) o).value);
        }
        return super.compareTo(o);
    }
}

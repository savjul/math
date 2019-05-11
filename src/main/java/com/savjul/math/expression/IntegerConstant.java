package com.savjul.math.expression;

import java.util.Objects;

public final class IntegerConstant extends AbstractBaseExpression {
    public static final IntegerConstant MINUS_ONE = IntegerConstant.of(-1);
    public static final IntegerConstant ZERO = IntegerConstant.of(0);
    public static final IntegerConstant ONE = IntegerConstant.of(1);
    private final Long value;

    private IntegerConstant(Expression parent, Long value) {
        super(parent);
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static IntegerConstant of(Long value) {
        return new IntegerConstant(null, value);
    }

    public static IntegerConstant of(Integer value) {
        return new IntegerConstant(null, value.longValue());
    }

    public Long getValue() {
        return value;
    }

    @Override
    public IntegerConstant withParent(Expression parent) {
        return new IntegerConstant(parent, this.value);
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String render() {
        return this.value.toString();
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

    @Override
    public int order() {
        return this.getParent() instanceof Term ? ExpressionConstants.INTEGER_ORDER_TERM
                : ExpressionConstants.INTEGER_ORDER_OTHER;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof IntegerConstant) {
            return this.value.compareTo(((IntegerConstant) o).value);
        }
        return super.compareTo(o);
    }
}

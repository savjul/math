package com.savjul.math.expression;

import java.util.Objects;

public final class IntegerConstant extends Expression {
    public static final IntegerConstant MINUS_ONE = IntegerConstant.of(-1);
    public static final IntegerConstant ZERO = IntegerConstant.of(0);
    public static final IntegerConstant ONE = IntegerConstant.of(1);
    public static final IntegerConstant TWO = IntegerConstant.of(2);
    private final Integer value;

    private IntegerConstant(Expression parent, Integer value) {
        super(parent);
        Objects.requireNonNull(value);
        this.value = value;
    }

    public static IntegerConstant of(Integer value) {
        return new IntegerConstant(null, value);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public IntegerConstant withParent(Expression parent) {
        return new IntegerConstant(parent, this.value);
    }

    public IntegerConstant add(IntegerConstant o) {
        return new IntegerConstant(null,this.value + o.value);
    }

    @Override
    public Expression add(Expression o) {
        if (o instanceof IntegerConstant) {
            return this.add((IntegerConstant) o);
        }
        else {
            return super.add(o);
        }
    }

    @Override
    public Expression multiply(Expression o) {
        if (o instanceof IntegerConstant) {
            return new IntegerConstant(null, this.value * ((IntegerConstant)o).value);
        }
        else {
            return super.multiply(o);
        }
    }

    @Override
    public String render() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntegerConstant integerConstant = (IntegerConstant) o;

        return value.equals(integerConstant.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
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

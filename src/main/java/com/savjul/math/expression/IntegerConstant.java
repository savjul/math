package com.savjul.math.expression;

import java.util.Collections;
import java.util.List;
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

    private IntegerConstant add(IntegerConstant o) {
        return new IntegerConstant(null,this.value + o.value);
    }

    @Override
    public Expression plus(Expression o) {
        // necessary to prevent infinite recursion in Expression::plus
        if (o instanceof IntegerConstant) {
            return this.add((IntegerConstant) o);
        }
        else {
            return super.plus(o);
        }
    }

    private IntegerConstant multiply(IntegerConstant o) {
        return new IntegerConstant(null,this.value * o.value);
    }

    @Override
    public Expression times(Expression o) {
        // Necessary to prevent constant ^ constants from accumulating in expressions
        if (o instanceof IntegerConstant) {
            return this.multiply((IntegerConstant) o);
        }
        else {
            return super.times(o);
        }
    }

    @Override
    public IntegerConstant getCoefficient() {
        return this;
    }

    @Override
    public List<Expression> getNonCoefficients() {
        return Collections.emptyList();
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

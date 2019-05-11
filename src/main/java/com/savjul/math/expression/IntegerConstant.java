package com.savjul.math.expression;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class IntegerConstant extends Expression {
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

    private IntegerConstant add(IntegerConstant o) {
        return IntegerConstant.of(this.value + o.value);
    }

    @Override
    public Expression plus(Expression o) {
        if (this.equals(IntegerConstant.ZERO)) {
            return o;
        }
        else if (o.equals(IntegerConstant.ZERO)) {
            return this;
        }
        else if (o instanceof IntegerConstant) {
            return this.add((IntegerConstant) o);
        }
        else {
            return super.plus(o);
        }
    }

    private IntegerConstant times(IntegerConstant o) {
        return IntegerConstant.of(this.value * o.value);
    }

    @Override
    public Expression times(Expression o) {
        if (this.equals(IntegerConstant.ZERO) || o.equals(IntegerConstant.ZERO)) {
            return IntegerConstant.ZERO;
        }
        else if (this.equals(IntegerConstant.ONE)) {
            return o;
        }
        else if (o.equals(IntegerConstant.ONE)) {
            return this;
        }
        else if (o instanceof IntegerConstant) {
            return this.times((IntegerConstant) o);
        }
        else {
            return super.times(o);
        }
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    private Expression pow(IntegerConstant o) {
        if (o.value == 0) {
            return IntegerConstant.ONE;
        }
        else if (o.value == 1) {
            return this;
        }
        else if (o.value > -1) {
            return IntegerConstant.of(pow(this.value, o.value));
        }
        else if (o.value < 0) {
            return IntegerConstant.of(pow(this.value, -1 * o.value)).invert();
        }
        else {
            return super.pow(o);
        }
    }

    private long pow(long base, long power) {
        long res = 1;
        for (long idx = 0; idx < power; idx++) {
            res *= base;
        }
        return res;
    }

    @Override
    public Expression pow(Expression o) {
        if (o instanceof IntegerConstant) {
            return this.pow((IntegerConstant) o);
        }
        else {
            return super.pow(o);
        }
    }

    @Override
    public Expression getCoefficient() {
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

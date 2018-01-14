package com.savjul.math.expression;

import java.util.Objects;

public final class IntegerConstant extends Expression {
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
            return Polynomial.of(this, o);
        }
    }

    @Override
    public Expression multiply(Expression other) {
        if (other instanceof IntegerConstant && this.value.getClass().isInstance(((IntegerConstant) other).value)) {
            IntegerConstant o = (IntegerConstant) other;
            return new IntegerConstant(null, this.value * o.value);
        }
        else {
            return Term.of(this, other);
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
        return ExpressionConstants.INTEGER_ORDER_TERM;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof IntegerConstant) {
            return this.value.compareTo(((IntegerConstant) o).value);
        }
        return super.compareTo(o);
    }
}

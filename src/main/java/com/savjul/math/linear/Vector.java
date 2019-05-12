package com.savjul.math.linear;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.IntegerConstant;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Vector {
    public static final Vector ZERO3 = Vector.of(IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ZERO);
    public static final Vector i = Vector.of(IntegerConstant.ONE, IntegerConstant.ZERO, IntegerConstant.ZERO);
    public static final Vector j = Vector.of(IntegerConstant.ZERO, IntegerConstant.ONE, IntegerConstant.ZERO);
    public static final Vector k = Vector.of(IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ONE);

    private final Expression[] values;

    private Vector(Expression[] values) {
        this.values = values;
    }

    Expression[] getValues() {
        return values;
    }

    public static Vector of(Expression... values) {
        return new Vector(Arrays.copyOf(values, values.length));
    }

    public Vector apply(Function<Expression, Expression> mapping) {
        Expression[] result = new Expression[this.values.length];
        for (int idx = 0; idx < result.length; idx++) {
            result[idx] = mapping.apply(this.values[idx]);
        }
        return new Vector(result);
    }

    public Vector simplify() {
        return apply(Expression::simplify);
    }

    public Vector plus(Vector o) {
        check(o);
        Expression[] result = new Expression[this.values.length];
        for (int idx = 0; idx < result.length; idx++) {
            result[idx] = this.values[idx].plus(o.values[idx]);
        }
        return new Vector(result);
    }

    public Vector times(Expression scalar) {
        Expression[] result = new Expression[this.values.length];
        for (int idx = 0; idx < result.length; idx++) {
            result[idx] = this.values[idx].times(scalar);
        }
        return new Vector(result);
    }

    public Expression dot(Vector o) {
        check(o);
        Expression result = IntegerConstant.ZERO;
        for (int idx = 0; idx < this.values.length; idx++) {
            result = result.plus(this.values[idx].times(o.values[idx]));
        }
        return result;
    }

    public Vector cross(Vector o) {
        Expression i1 = this.values[1].times(o.values[2]);
        Expression i2 = this.values[2].times(o.values[1]);
        Vector i3 = i.times(i1.plus(IntegerConstant.MINUS_ONE.times(i2)));

        Expression j1 = this.values[2].times(o.values[0]);
        Expression j2 = this.values[0].times(o.values[2]);
        Vector j3 = j.times(j1.plus(IntegerConstant.MINUS_ONE.times(j2)));

        Expression k1 = this.values[0].times(o.values[1]);
        Expression k2 = this.values[1].times(o.values[0]);
        Vector k3 = k.times(k1.plus(IntegerConstant.MINUS_ONE.times(k2)));

        return i3.plus(j3).plus(k3);
    }

    private int size() {
        return this.values.length;
    }

    private Expression get(int idx) {
        return this.values[idx];
    }

    private void check(Vector o) {
        if (this.values.length != o.values.length) {
            throw new RuntimeException("Vector size mismatch: "
                    + this.values.length + " vs " + o.values.length);
        }
    }

    @Override
    public String toString() {
        return "[" + String.join(", ",
                Stream.of(this.values).map(Expression::toString).collect(Collectors.toList())) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        return Arrays.equals(values, vector.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}

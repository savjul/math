package com.savjul.math.linear;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.IntegerConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Vector {
    public static final Vector ZERO3 = Vector.of(IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ZERO);
    public static final Vector i = Vector.of(IntegerConstant.ONE, IntegerConstant.ZERO, IntegerConstant.ZERO);
    public static final Vector j = Vector.of(IntegerConstant.ZERO, IntegerConstant.ONE, IntegerConstant.ZERO);
    public static final Vector k = Vector.of(IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ONE);

    private final List<Expression> values;

    private Vector(List<Expression> values) {
        this.values = values;
    }

    public static Vector of(Expression... values) {
        return new Vector(Arrays.asList(values));
    }

    public Vector simplify() {
        return new Vector(this.values.stream().map(Expression::simplify).collect(Collectors.toList()));
    }

    public Vector add(Vector o) {
        check(o);
        List<Expression> result = new ArrayList<>();
        for (int idx = 0; idx < this.size(); idx++) {
            result.add(this.get(idx).add(o.get(idx)));
        }
        return new Vector(result);
    }

    public Vector multiply(Expression scalar) {
        return new Vector(this.values.stream().map(scalar::multiply).collect(Collectors.toList()));
    }

    public Expression dot(Vector o) {
        check(o);
        Expression result = IntegerConstant.ZERO;
        for (int idx = 0; idx < this.size(); idx++) {
            result = result.add(this.get(idx).multiply(o.get(idx)));
        }
        return result;
    }

    public Vector cross(Vector o) {
        Expression i1 = this.get(1).multiply(o.get(2));
        Expression i2 = this.get(2).multiply(o.get(1));
        Vector i3 = i.multiply(i1.add(IntegerConstant.MINUS_ONE.multiply(i2)));

        Expression j1 = this.get(2).multiply(o.get(0));
        Expression j2 = this.get(0).multiply(o.get(2));
        Vector j3 = j.multiply(j1.add(IntegerConstant.MINUS_ONE.multiply(j2)));

        Expression k1 = this.get(0).multiply(o.get(1));
        Expression k2 = this.get(1).multiply(o.get(0));
        Vector k3 = k.multiply(k1.add(IntegerConstant.MINUS_ONE.multiply(k2)));

        return i3.add(j3).add(k3);
    }

    private int size() {
        return this.values.size();
    }

    private Expression get(int idx) {
        return this.values.get(idx);
    }

    private void check(Vector o) {
        if (this.size() != o.size()) {
            throw new RuntimeException("Vector size mismatch: "
                    + this.size() + " vs " + o.size());
        }
    }

    @Override
    public String toString() {
        return "[" + String.join(", ",
                this.values.stream().map(Expression::toString).collect(Collectors.toList())) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector vector = (Vector) o;

        return values.equals(vector.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
}

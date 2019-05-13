package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.IntegerConstant;

import java.util.Objects;

public final class Exponent extends AbstractBaseExpression {
    private final Expression base;
    private final Expression power;

    private Exponent(Expression base, Expression power) {
        Objects.requireNonNull(base);
        Objects.requireNonNull(power);
        this.base = base;
        this.power = power;
    }

    public static Exponent of(Expression base, Expression exponent) {
        return new Exponent(base, exponent);
    }

    public static Expression getBase(Expression expression) {
        if (expression instanceof Exponent) {
            return ((Exponent) expression).getBase();
        }
        else {
            return expression;
        }
    }

    public static Expression getPower(Expression expression) {
        if (expression instanceof Exponent) {
            return ((Exponent) expression).getPower();
        }
        else {
            return IntegerConstant.ONE;
        }
    }

    public Expression getBase() {
        return base;
    }

    public Expression getPower() {
        return power;
    }

    @Override
    public boolean isConstant() {
        return this.base.isConstant() && this.power.isConstant();
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exponent exponent1 = (Exponent) o;

        if (!base.equals(exponent1.base)) return false;
        return power.equals(exponent1.power);
    }

    @Override
    public int hashCode() {
        int result = base.hashCode();
        result = 31 * result + power.hashCode();
        return result;
    }
}

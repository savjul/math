package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.Constant;

import java.util.Objects;

public final class Rational extends AbstractBaseExpression {
    private final Expression numerator;
    private final Expression denominator;

    private Rational(Expression numerator, Expression denominator) {
        Objects.requireNonNull(numerator);
        Objects.requireNonNull(denominator);
        if (denominator.equals(Constant.ZERO)) {
            throw new RuntimeException("Division by zero");
        }
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Rational of(Expression numerator, Expression denominator) {
        return new Rational(numerator, denominator);
    }


    @Override
    public Expression invert() {
        return Rational.of(this.denominator, this.numerator);
    }

    @Override
    public boolean isConstant() {
        return this.numerator.isConstant() && this.denominator.isConstant();
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    public Expression getNumerator() {
        return numerator;
    }

    public Expression getDenominator() {
        return denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rational rational = (Rational) o;

        if (!numerator.equals(rational.numerator)) return false;
        return denominator.equals(rational.denominator);
    }

    @Override
    public int hashCode() {
        int result = numerator.hashCode();
        result = 31 * result + denominator.hashCode();
        return result;
    }
}

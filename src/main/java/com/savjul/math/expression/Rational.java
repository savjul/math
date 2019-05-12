package com.savjul.math.expression;

import java.util.Objects;

public final class Rational extends AbstractBaseExpression {
    private final Expression numerator;
    private final Expression denominator;

    private Rational(Expression numerator, Expression denominator) {
        Objects.requireNonNull(numerator);
        Objects.requireNonNull(denominator);
        if (denominator.equals(IntegerConstant.ZERO)) {
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
    public Expression withContext(Context context) {
        return Rational.of(this.numerator.withContext(context), this.denominator.withContext(context));
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

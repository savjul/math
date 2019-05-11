package com.savjul.math.expression;

import java.util.Objects;

public final class Rational extends AbstractBaseExpression {
    private final Expression numerator;
    private final Expression denominator;

    private Rational(Expression parent, Expression numerator, Expression denominator) {
        super(parent);
        Objects.requireNonNull(numerator);
        Objects.requireNonNull(denominator);
        if (denominator.equals(IntegerConstant.ZERO)) {
            throw new RuntimeException("Division by zero");
        }
        this.numerator = numerator.withParent(this);
        this.denominator = denominator.withParent(this);
    }

    public static Expression of(Expression numerator, Expression denominator) {
        if (numerator.equals(IntegerConstant.ZERO)) {
            return IntegerConstant.ZERO;
        }
        else if (denominator.equals(IntegerConstant.ONE)) {
            return numerator;
        }
        else {
            return new Rational(null, numerator, denominator);
        }
    }


    @Override
    public Expression withParent(Expression parent) {
        return new Rational(parent, this.numerator, this.denominator);
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
    public Expression times(Expression o) {
        if (o instanceof Rational) {
            Rational other = (Rational) o;
            return Rational.of(this.numerator.times(other.numerator), this.denominator.times(other.denominator));
        }
        else {
            return Rational.of(o.times(this.numerator), this.denominator);
        }
    }

    @Override
    public Expression divideBy(Expression o) {
        if (o instanceof Rational) {
            Rational other = (Rational) o;
            return Rational.of(this.numerator.times(other.denominator), this.denominator.times(other.numerator));
        }
        else {
            return super.divideBy(o);
        }
    }

    @Override
    public Expression withContext(Context context) {
        return Rational.of(this.numerator.withContext(context), this.denominator.withContext(context));
    }

    @Override
    public Expression simplify() {
        Expression numerator = this.numerator.simplify();
        Expression denominator = this.denominator.simplify();
        return Rational.of(numerator, denominator);
    }

    @Override
    public String render() {
        return numerator.render() + "/" + denominator.render();
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

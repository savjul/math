package com.savjul.math.expression;

public abstract class AbstractBaseExpression implements Expression {
    private final Expression parent;

    protected AbstractBaseExpression(Expression parent) {
        this.parent = parent;
    }

    @Override
    public Expression withContext(Context context) {
        return this;
    }

    @Override
    public Expression plus(Expression o) {
        return Polynomial.of(this, o);
    }

    @Override
    public Expression times(Expression o) {
        return Term.of(this, o);
    }

    @Override
    public Expression divideBy(Expression o) {
        return Rational.of(this, o);
    }

    @Override
    public Expression invert() {
        return Rational.of(IntegerConstant.ONE, this);
    }

    @Override
    public Expression pow(Expression o) {
        return Exponent.of(this, o);
    }

    @Override
    public Expression simplify() {
        return BasicSimplifier.instance().simplify(this);
    }

    @Override
    public Expression getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return this.render();
    }
}

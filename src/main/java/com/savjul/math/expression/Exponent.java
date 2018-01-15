package com.savjul.math.expression;

import java.util.Objects;

public final class Exponent extends Expression {
    private final Expression base;
    private final Expression power;

    private Exponent(Expression parent, Expression base, Expression power) {
        super(parent);
        Objects.requireNonNull(base);
        Objects.requireNonNull(power);
        this.base = base.withParent(this);
        this.power = power.withParent(this);
    }

    public static Exponent of(Expression base, Expression exponent) {
        return new Exponent(null, base, exponent);
    }

    @Override
    public Expression getBase() {
        return base;
    }

    @Override
    public Expression getPower() {
        return power;
    }

    @Override
    public Exponent withParent(Expression parent) {
        return new Exponent(parent, this.base, this.power);
    }

    @Override
    public Expression simplify() {
        return new Exponent(null, this.base.simplify(), this.power.simplify());
    }

    @Override
    public String render() {
        return this.base.render() + "^" + this.power.render();
    }

    @Override
    public int order() {
        return ExpressionConstants.EXPONENT_ORDER;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Exponent) {
            Exponent other = (Exponent) o;
            int result = this.base.compareTo(other.base);
            return result != 0 ? result : this.power.compareTo(other.power);
        }
        return super.compareTo(o);
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

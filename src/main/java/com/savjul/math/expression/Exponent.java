package com.savjul.math.expression;

import java.util.Objects;

public final class Exponent extends Expression {
    private final Expression base;
    private final Expression exponent;

    private Exponent(Expression parent, Expression base, Expression exponent) {
        super(parent);
        Objects.requireNonNull(base);
        Objects.requireNonNull(exponent);
        this.base = base.withParent(this);
        this.exponent = exponent.withParent(this);
    }

    public static Exponent of(Expression base, Expression exponent) {
        return new Exponent(null, base, exponent);
    }

    public Expression getBase() {
        return base;
    }

    public Expression getExponent() {
        return exponent;
    }

    @Override
    public Exponent withParent(Expression parent) {
        return new Exponent(parent, this.base, this.exponent);
    }

    @Override
    public Expression add(Expression o) {
        if (this.equals(o)) {
            return IntegerConstant.TWO.multiply(this);
        }
        else {
            return super.add(o);
        }
    }

    @Override
    public Expression multiply(Expression other) {
        if (other instanceof Exponent) {
            Exponent o = (Exponent) other;
            if (this.base.equals(o.base)) {
                return Exponent.of(this.base, this.exponent.add(o.exponent));
            }
        }
        return super.multiply(other);
    }

    @Override
    public Expression simplify() {
        return new Exponent(null, this.base.simplify(), this.exponent.simplify());
    }

    @Override
    public String render() {
        return this.base.render() + "^" + this.exponent.render();
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
            return result != 0 ? result : this.exponent.compareTo(other.exponent);
        }
        return super.compareTo(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exponent exponent1 = (Exponent) o;

        if (!base.equals(exponent1.base)) return false;
        return exponent.equals(exponent1.exponent);
    }

    @Override
    public int hashCode() {
        int result = base.hashCode();
        result = 31 * result + exponent.hashCode();
        return result;
    }
}

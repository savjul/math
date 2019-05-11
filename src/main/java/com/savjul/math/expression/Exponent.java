package com.savjul.math.expression;

import java.util.Objects;

public final class Exponent extends AbstractBaseExpression {
    private final Expression base;
    private final Expression power;

    private Exponent(Expression parent, Expression base, Expression power) {
        super(parent);
        Objects.requireNonNull(base);
        Objects.requireNonNull(power);
        this.base = base.withParent(this);
        this.power = power.withParent(this);
    }

    public static Expression of(Expression base, Expression exponent) {
        if (base.equals(IntegerConstant.ONE)) {
            return IntegerConstant.ONE;
        }
        else if (base.equals(IntegerConstant.ZERO)) {
            if (exponent.equals(IntegerConstant.ZERO)) {
                throw new RuntimeException("Can't evaluate 0^0");
            }
            else {
                return IntegerConstant.ZERO;
            }
        }
        else if (exponent.equals(IntegerConstant.ONE)) {
            return base;
        }
        else if (exponent.equals(IntegerConstant.ZERO)) {
            return IntegerConstant.ONE;
        }
        else {
            return new Exponent(null, base, exponent);
        }
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
    public boolean isConstant() {
        return this.base.isConstant() && this.power.isConstant();
    }

    @Override
    public Exponent withParent(Expression parent) {
        return new Exponent(parent, this.base, this.power);
    }

    @Override
    public Expression withContext(Context context) {
        return new Exponent(null, this.base.withContext(context), this.power.withContext(context));
    }

    @Override
    public Expression simplify() {
        return this.base.simplify().pow(this.power.simplify());
    }

    @Override
    public String render() {
        String result = this.base.render() + "^" + this.power.render();
        return getParent() instanceof Term && ! (this.power instanceof IntegerConstant) ? "(" + result + ")" : result;
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

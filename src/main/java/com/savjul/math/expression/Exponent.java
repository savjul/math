package com.savjul.math.expression;

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
    public Expression withContext(Context context) {
        return new Exponent(this.base.withContext(context), this.power.withContext(context));
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

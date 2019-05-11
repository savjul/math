package com.savjul.math.expression;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DoubleConstant extends AbstractBaseExpression {
    public static final DoubleConstant ZERO = new DoubleConstant(null, 0.0);
    public static final DoubleConstant ONE = new DoubleConstant(null, 1.0);

    private final Double value;

    public static Expression of(Double value) {
        return new DoubleConstant(null, value);
    }

    private DoubleConstant(Expression parent, Double value) {
        super(parent);
        this.value = value;
    }

    @Override
    public Expression withParent(Expression parent) {
        return new DoubleConstant(parent, value);
    }

    @Override
    public Expression plus(Expression o) {
        if (o instanceof DoubleConstant) {
            if (this.equals(ZERO)) return o;
            if (o.equals(ZERO)) return this;
            return new DoubleConstant(null, value + ((DoubleConstant) o).value);
        }
        else if (o instanceof IntegerConstant) {
            IntegerConstant oi = (IntegerConstant) o;
            if (oi.equals(IntegerConstant.ZERO)) return this;
            return new DoubleConstant(null, value + oi.getValue());
        }
        return super.plus(o);
    }

    @Override
    public Expression times(Expression o) {
        if (o instanceof DoubleConstant) {
            return new DoubleConstant(null, value * ((DoubleConstant) o).value);
        }
        else if (o instanceof IntegerConstant) {
            IntegerConstant oi = (IntegerConstant) o;
            if (oi.equals(IntegerConstant.ONE)) return this;
            return new DoubleConstant(null, value + oi.getValue());
        }
        return super.times(o);
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Expression pow(Expression o) {
        if (o instanceof DoubleConstant) {
            return pow((DoubleConstant) o);
        }
        else if (o instanceof IntegerConstant) {
            return pow((IntegerConstant) o);
        }
        else {
            return super.pow(o);
        }
    }

    private Expression pow(DoubleConstant o) {
        if (o.equals(DoubleConstant.ZERO)) {
            return DoubleConstant.ONE;
        } else if (o.equals(DoubleConstant.ONE)) {
            return this;
        }
        return new DoubleConstant(null, Math.pow(value, o.value));
    }

    private Expression pow(IntegerConstant o) {
        if (o.equals(IntegerConstant.ZERO)) {
            return DoubleConstant.ONE;
        } else if (o.equals(IntegerConstant.ONE)) {
            return this;
        } else {
            return new DoubleConstant(null, Math.pow(value, o.getValue()));
        }
    }

    @Override
    public Expression getCoefficient() {
        return this;
    }

    @Override
    public List<Expression> getNonCoefficients() {
        return Collections.emptyList();
    }

    @Override
    public String render() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleConstant that = (DoubleConstant) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int order() {
        return this.getParent() instanceof Term ? ExpressionConstants.DOUBLE_ORDER_TERM
                : ExpressionConstants.DOUBLE_ORDER_OTHER;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof DoubleConstant) {
            return this.value.compareTo(((DoubleConstant) o).value);
        }
        return super.compareTo(o);
    }
}

package com.savjul.math.expression.simple;

import com.savjul.math.expression.AbstractBaseExpression;

public abstract class NumericConstant extends AbstractBaseExpression {
    public abstract Number getValue();

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public String toString() {
        return getValue().toString();
    }
}

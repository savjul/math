package com.savjul.math.expression;

public abstract class NumericConstant extends AbstractBaseExpression {
    public abstract Number getValue();

    @Override
    public boolean isConstant() {
        return true;
    }
}

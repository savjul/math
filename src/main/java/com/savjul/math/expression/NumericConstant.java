package com.savjul.math.expression;

public abstract class NumericConstant extends AbstractBaseExpression {
    protected NumericConstant(Expression parent) {
        super(parent);
    }

    public abstract Number getValue();

    @Override
    public boolean isConstant() {
        return true;
    }
}

package com.savjul.math.expression;

public interface Expression {
    Expression withContext(Context context);

    Expression plus(Expression o);

    Expression times(Expression o);

    Expression divideBy(Expression o);

    Expression invert();

    boolean isConstant();

    Expression pow(Expression o);

    Expression simplify();
}

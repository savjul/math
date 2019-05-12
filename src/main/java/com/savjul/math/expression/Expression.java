package com.savjul.math.expression;

public interface Expression {
    Expression withParent(Expression parent);

    Expression withContext(Context context);

    String render();

    Expression plus(Expression o);

    Expression times(Expression o);

    Expression divideBy(Expression o);

    Expression invert();

    boolean isConstant();

    Expression pow(Expression o);

    Expression simplify();

    Expression getParent();
}

package com.savjul.math.expression;

public interface Expression extends Comparable<Expression> {
    Expression withParent(Expression parent);

    Expression withContext(Context context);

    String render();

    int order();

    Expression plus(Expression o);

    Expression times(Expression o);

    Expression divideBy(Expression o);

    Expression invert();

    boolean isConstant();

    Expression pow(Expression o);

    Expression simplify();

    Expression getParent();
}

package com.savjul.math.expression;

import com.savjul.math.transformers.Visitor;

public interface Expression {
    Expression plus(Expression o);

    Expression times(Expression o);

    Expression divideBy(Expression o);

    Expression invert();

    boolean isConstant();

    boolean isCompound();

    Expression pow(Expression o);

    Expression simplify();

    <T> void visit(T state, Visitor<T> visitor);
}

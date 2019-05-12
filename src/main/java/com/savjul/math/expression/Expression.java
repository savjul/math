package com.savjul.math.expression;

import java.util.function.Function;

public interface Expression {
    Expression plus(Expression o);

    Expression times(Expression o);

    Expression divideBy(Expression o);

    Expression invert();

    boolean isConstant();

    boolean isCompound();

    Expression pow(Expression o);

    Expression simplify();

    default Expression apply(Function<Expression, Expression> transformation) {
        return transformation.apply(this);
    }
}

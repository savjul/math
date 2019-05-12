package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;

public interface Visitor<T> {
    void visit(T state, Expression expression, Expression parent);
}

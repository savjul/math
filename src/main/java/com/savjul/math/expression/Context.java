package com.savjul.math.expression;

public interface Context {
    Expression getValue(String name, Expression defaultValue);
}

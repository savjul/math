package com.savjul.math.expression;

import java.util.Objects;

public final class Variable extends AbstractBaseExpression {
    private final String name;

    private Variable(Expression parent, String name) {
        super(parent);
        Objects.requireNonNull(name);
        this.name = name;
    }

    public static Expression of(String name) {
        return new Variable(null, name);
    }

    public String getName() {
        return name;
    }

    @Override
    public Variable withParent(Expression parent) {
        return new Variable(parent, this.name);
    }

    @Override
    public Expression withContext(Context context) {
        return context.getValue(this.name, this);
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public String render() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

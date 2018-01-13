package com.savjul.math.expression;

import java.util.Objects;

public final class Variable extends Expression {
    private final String name;

    private Variable(Expression parent, String name) {
        super(parent);
        Objects.requireNonNull(name);
        this.name = name;
    }

    public static Variable of(String name) {
        return new Variable(null, name);
    }

    @Override
    public Variable of(Expression parent) {
        return new Variable(parent, this.name);
    }

    @Override
    public Expression add(Expression other) {
        if (this.equals(other)) {
            return Term.of(IntegerConstant.of(2), this);
        }
        else {
            return Polynomial.of(this, other);
        }
    }

    @Override
    public Expression multiply(Expression other) {
        if (this.equals(other)) {
            return Exponent.of(this, IntegerConstant.of(2));
        }
        else {
            return Term.of(this, other);
        }
    }

    @Override
    public int order() {
        return ExpressionConstants.VARIABLE_ORDER;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Variable) {
            return this.name.compareTo(((Variable) o).name);
        }
        return super.compareTo(o);
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

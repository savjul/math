package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;

public final class Calculator extends ExpressionVisitor<Double> {
    private static final Calculator INSTANCE = new Calculator();

    public static Calculator instance() { return INSTANCE; }

    public static Double doubleValue(Expression expression) {
        return INSTANCE.visit(expression);
    }

    @Override
    public Double visit(Term expression) {
        return expression.getFactors().stream().map(this::visit).sorted().reduce(1.0, (a, b) -> a * b);
    }

    @Override
    public Double visit(Polynomial expression) {
        return expression.getTerms().stream().map(this::visit).sorted().reduce(0.0, (a, b) -> a + b);
    }

    @Override
    public Double visit(Exponent expression) {
        return Math.pow(visit(expression.getBase()), visit(expression.getPower()));
    }

    @Override
    public Double visit(Rational expression) {
        return visit(expression.getNumerator()) / visit(expression.getDenominator());
    }

    @Override
    public Double visit(Trigonometric expression) {
        return expression.doubleValue(this::visit);
    }

    @Override
    public Double visit(Constant<?> expression) {
        return expression.getValue().doubleValue();
    }

    @Override
    public Double visit(Transcendental expression) {
        return expression.getValue();
    }

    @Override
    public Double visit(Variable expression) {
        throw new IllegalThreadStateException("Can't convert variable to Double");
    }

    private Calculator() {}
}

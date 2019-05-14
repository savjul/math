package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;

public class ExpressionVisitor<T>  {
    public T visit(Expression expression, Expression parent) {
        if (expression instanceof Term) return visit((Term) expression, parent);
        else if (expression instanceof Polynomial) return visit((Polynomial) expression, parent);
        else if (expression instanceof Exponent) return visit((Exponent) expression, parent);
        else if (expression instanceof Rational) return visit((Rational) expression, parent);
        else if (expression instanceof Transcendental) return visit((Transcendental) expression, parent);
        else if (expression instanceof Constant) return visit((Constant) expression, parent);
        else if (expression instanceof Trigonometric) return visit((Trigonometric) expression, parent);
        else if (expression instanceof Variable) return visit((Variable) expression, parent);
        return defaultValue(expression, parent);
    }

    public T defaultValue(Expression expression, Expression parent) {
        return null;
    }

    public T visit(Term expression, Expression parent) {
        for (Expression factor: expression.getFactors()) {
            visit(factor, expression);
        }
        return defaultValue(expression, parent);
    }

    public T visit(Polynomial expression, Expression parent) {
        for (Expression factor: expression.getTerms()) {
            visit(factor, expression);
        }
        return defaultValue(expression, parent);
    }

    public T visit(Exponent expression, Expression parent) {
        visit(expression.getBase(), expression);
        visit(expression.getPower(), expression);
        return defaultValue(expression, parent);
    }

    public T visit(Rational expression, Expression parent) {
        visit(expression.getNumerator(), expression);
        visit(expression.getDenominator(), expression);
        return defaultValue(expression, parent);
    }

    public T visit(Constant<?> expression, Expression parent) {
        return defaultValue(expression, parent);
    }

    public T visit(Transcendental expression, Expression parent) {
        return defaultValue(expression, parent);
    }

    public T visit(Trigonometric expression, Expression parent) {
        visit(expression.getArgument(), expression);
        return defaultValue(expression, parent);
    }

    public T visit(Variable expression, Expression parent) {
        return defaultValue(expression, parent);
    }
}

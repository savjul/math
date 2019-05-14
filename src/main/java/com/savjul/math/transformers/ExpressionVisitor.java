package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;

public class ExpressionVisitor<T>  {
    public T visit(Expression expression) {
        if (expression instanceof Term) return visit((Term) expression);
        else if (expression instanceof Polynomial) return visit((Polynomial) expression);
        else if (expression instanceof Exponent) return visit((Exponent) expression);
        else if (expression instanceof Rational) return visit((Rational) expression);
        else if (expression instanceof Transcendental) return visit((Transcendental) expression);
        else if (expression instanceof Constant) return visit((Constant) expression);
        else if (expression instanceof Trigonometric) return visit((Trigonometric) expression);
        else if (expression instanceof Variable) return visit((Variable) expression);
        return defaultValue(expression);
    }

    public T visit(Term expression) {
        for (Expression factor: expression.getFactors()) {
            visit(factor);
        }
        return defaultValue(expression);
    }

    public T visit(Polynomial expression) {
        for (Expression factor: expression.getTerms()) {
            visit(factor);
        }
        return defaultValue(expression);
    }

    public T visit(Exponent expression) {
        visit(expression.getBase());
        visit(expression.getPower());
        return defaultValue(expression);
    }

    public T visit(Rational expression) {
        visit(expression.getNumerator());
        visit(expression.getDenominator());
        return defaultValue(expression);
    }

    public T visit(Trigonometric expression) {
        visit(expression.getArgument());
        return defaultValue(expression);
    }

    public T visit(Constant<?> expression) {
        return defaultValue(expression);
    }

    public T visit(Transcendental expression) {
        return defaultValue(expression);
    }

    public T visit(Variable expression) {
        return defaultValue(expression);
    }

    public T defaultValue(Expression expression) {
        return null;
    }
}

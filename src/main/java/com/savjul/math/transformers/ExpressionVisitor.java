package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;
import com.savjul.math.expression.simple.DoubleConstant;
import com.savjul.math.expression.simple.IntegerConstant;
import com.savjul.math.expression.simple.NumericConstant;
import com.savjul.math.expression.simple.Variable;

public class ExpressionVisitor<T>  {
    public T visit(Expression expression, Expression parent) {
        if (expression instanceof Term) return visit((Term) expression, parent);
        else if (expression instanceof Polynomial) return visit((Polynomial) expression, parent);
        else if (expression instanceof Exponent) return visit((Exponent) expression, parent);
        else if (expression instanceof Rational) return visit((Rational) expression, parent);
        else if (expression instanceof NumericConstant) return visit((NumericConstant) expression, parent);
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

    public T visit(NumericConstant expression, Expression parent) {
        if (expression instanceof DoubleConstant) return visit((DoubleConstant) expression, parent);
        if (expression instanceof IntegerConstant) return visit((IntegerConstant) expression, parent);
        return defaultValue(expression, parent);
    }

    public T visit(DoubleConstant expression, Expression parent) {
        return defaultValue(expression, parent);
    }
    public T visit(IntegerConstant expression, Expression parent) {
        return defaultValue(expression, parent);
    }
    public T visit(Variable expression, Expression parent) {
        return defaultValue(expression, parent);
    }
}

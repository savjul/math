package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;

public class TopDownVisitor<T> implements Visitor<T> {
    @Override
    public void visit(T state, Expression expression, Expression parent) {
        if (expression instanceof Term) visit(state, (Term) expression, parent);
        else if (expression instanceof Polynomial) visit(state, (Polynomial) expression, parent);
        else if (expression instanceof Exponent) visit(state, (Exponent) expression, parent);
        else if (expression instanceof Rational) visit(state, (Rational) expression, parent);
    }

    public void visit(T state, Term expression, Expression parent) {
        for (Expression factor: expression.getFactors()) {
            visit(state, factor, expression);
        }
    }

    public void visit(T state, Polynomial expression, Expression parent) {
        for (Expression factor: expression.getTerms()) {
            visit(state, factor, expression);
        }
    }

    public void visit(T state, Exponent expression, Expression parent) {
        visit(state, expression.getBase(), expression);
        visit(state, expression.getPower(), expression);
    }

    public void visit(T state, Rational expression, Expression parent) {
        visit(state, expression.getNumerator(), expression);
        visit(state, expression.getDenominator(), expression);
    }
}

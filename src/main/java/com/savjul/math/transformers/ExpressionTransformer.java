package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;

public class ExpressionTransformer extends ExpressionVisitor<Expression> {
    @Override
    public Expression visit(Expression expression) {
        return super.visit(expression);
    }

    @Override
    public Expression visit(Term expression) {
        return Term.of(expression.getFactors().stream().map(this::visit));
    }

    @Override
    public Expression visit(Polynomial expression) {
        return Polynomial.of(expression.getTerms().stream().map(this::visit));
    }

    @Override
    public Expression visit(Exponent expression) {
        return Exponent.of(visit(expression.getBase()), visit(expression.getPower()));
    }

    @Override
    public Expression visit(Rational expression) {
        return Rational.of(visit(expression.getNumerator()), visit(expression.getDenominator()));
    }

    @Override
    public Expression visit(Trigonometric expression) {
        return super.visit(expression.withArgument(visit(expression.getArgument())));
    }

    @Override
    public Expression visit(Constant<?> expression) {
        return super.visit(expression);
    }

    @Override
    public Expression visit(Transcendental expression) {
        return super.visit(expression);
    }

    @Override
    public Expression visit(Variable expression) {
        return super.visit(expression);
    }

    @Override
    public Expression defaultValue(Expression expression) {
        return expression;
    }
}

package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;

import java.util.List;
import java.util.function.Function;

public final class ExpressionRenderer extends ExpressionVisitor<Void> {
    private static final Function<Expression, String> INSTANCE = ExpressionRenderer::render;
    private final StringBuilder sb;

    private ExpressionRenderer(StringBuilder sb) {
        this.sb = sb;
    }

    public static Function<Expression, String> instance() {
        return INSTANCE;
    }

    private static String render(Expression expression) {
        StringBuilder sb = new StringBuilder();
        new ExpressionRenderer(sb).visit(expression, null);
        return sb.toString();
    }

    @Override
    public Void visit(Expression expression, Expression parent) {
        super.visit(expression, parent);
        return null;
    }

    @Override
    public Void visit(Term expression, Expression parent) {
        super.visit(expression, parent);
        return null;
    }

    @Override
    public Void visit(Polynomial expression, Expression parent) {
        if (parent instanceof Term || parent instanceof Exponent || parent instanceof Rational) sb.append('(');
        List<Expression> terms = expression.getTerms();
        int max = terms.size() - 1;
        for (int idx = 0; idx <= max; idx++) {
            visit(terms.get(idx), expression);
            if (idx != max) sb.append(" + ");
        }
        if (parent instanceof Term || parent instanceof Exponent || parent instanceof Rational) sb.append(')');
        return null;
    }

    @Override
    public Void visit(Exponent expression, Expression parent) {
        if (parent instanceof Term) sb.append('(');
        visit(expression.getBase(), expression);
        sb.append('^');
        visit(expression.getPower(), expression);
        if (parent instanceof Term) sb.append(')');
        return null;
    }

    @Override
    public Void visit(Transcendental expression, Expression parent) {
        sb.append(expression.getName());
        return null;
    }

    @Override
    public Void visit(Trigonometric expression, Expression parent) {
        sb.append(expression.getName()).append('(');
        visit(expression.getArgument(), parent);
        sb.append(')');
        return null;
    }

    @Override
    public Void visit(Rational expression, Expression parent) {
        visit(expression.getNumerator(), expression);
        sb.append('/');
        visit(expression.getDenominator(), expression);
        return null;
    }

    @Override
    public Void visit(Constant<?> expression, Expression parent) {
        sb.append(expression.getValue());
        return null;
    }

    @Override
    public Void visit(Variable expression, Expression parent) {
        sb.append(expression.getName());
        return null;
    }
}

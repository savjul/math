package com.savjul.math.expression;

import java.util.List;

public final class BasicRenderer {
    private static final Renderer INSTANCE = BasicRenderer::render;

    public static Renderer instance() {
        return INSTANCE;
    }

    private static String render(Expression expression) {
        StringBuilder sb = new StringBuilder();
        render(sb, expression, null);
        return sb.toString();
    }

    private static void render(StringBuilder sb, Expression expression, Expression parent) {
        if (expression instanceof Variable) {
            render(sb, (Variable) expression, parent);
        }
        else if (expression instanceof NumericConstant) {
            render(sb, (NumericConstant) expression, parent);
        }
        else if (expression instanceof Exponent) {
            render(sb, (Exponent) expression, parent);
        }
        else if (expression instanceof Rational) {
            render(sb, (Rational) expression, parent);
        }
        else if (expression instanceof Polynomial) {
            render(sb, (Polynomial) expression, parent);
        }
        else if (expression instanceof Term) {
            render(sb, (Term) expression, parent);
        }
        else {
            sb.append(expression.toString());
        }
    }

    private static void render(StringBuilder sb, Variable expression, Expression parent) {
        sb.append(expression.getName());
    }

    private static void render(StringBuilder sb, NumericConstant expression, Expression parent) {
        sb.append(expression.getValue());
    }

    private static void render(StringBuilder sb, Exponent expression, Expression parent) {
        if (parent instanceof Term) sb.append('(');
        render(sb, expression.getBase(), expression);
        sb.append('^');
        render(sb, expression.getPower(), expression);
        if (parent instanceof Term) sb.append(')');
    }

    private static void render(StringBuilder sb, Rational expression, Expression parent) {
        render(sb, expression.getNumerator(), expression);
        sb.append('/');
        render(sb, expression.getDenominator(), expression);
    }

    private static void render(StringBuilder sb, Polynomial expression, Expression parent) {
        if (parent instanceof Term || parent instanceof Exponent || parent instanceof Rational) sb.append('(');
        List<Expression> terms = expression.getTerms();
        int max = terms.size() - 1;
        for (int idx = 0; idx <= max; idx++) {
            render(sb, terms.get(idx), expression);
            if (idx != max) sb.append(" + ");
        }
        if (parent instanceof Term || parent instanceof Exponent || parent instanceof Rational) sb.append(')');
    }

    private static void render(StringBuilder sb, Term expression, Expression parent) {
        List<Expression> factors = expression.getFactors();
        int max = factors.size() - 1;
        for (int idx = 0; idx <= max; idx++) {
            render(sb, factors.get(idx), expression);
        }
    }

    private BasicRenderer() {}
}

package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;

import java.util.List;
import java.util.function.Function;

public final class ExpressionRenderer extends ExpressionVisitor<Void> {
    private static final Function<Expression, String> INSTANCE = ExpressionRenderer::render;
    private final StringBuilder sb;

    public ExpressionRenderer(StringBuilder sb) {
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
        if (!expression.isCompound()) {
            sb.append(expression.toString());
        }
        else {
            super.visit(expression, parent);
        }
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
    public Void visit(Rational expression, Expression parent) {
        visit(expression.getNumerator(), expression);
        sb.append('/');
        visit(expression.getDenominator(), expression);
        return null;
    }
}

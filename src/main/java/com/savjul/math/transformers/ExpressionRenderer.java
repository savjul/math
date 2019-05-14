package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ExpressionRenderer extends ExpressionVisitor<Void> {
    private static final Function<Expression, String> INSTANCE = ExpressionRenderer::render;
    private final List<Expression> context;
    private final StringBuilder sb;

    private ExpressionRenderer(StringBuilder sb) {
        this.context = new ArrayList<>();
        this.sb = sb;
    }

    public static Function<Expression, String> instance() {
        return INSTANCE;
    }

    private static String render(Expression expression) {
        StringBuilder sb = new StringBuilder();
        new ExpressionRenderer(sb).visit(expression);
        return sb.toString();
    }

    @Override
    public Void visit(Expression expression) {
        this.context.add(expression);
        super.visit(expression);
        this.context.remove(this.context.size() - 1);
        return null;
    }

    @Override
    public Void visit(Term expression) {
        super.visit(expression);
        return null;
    }

    @Override
    public Void visit(Polynomial expression) {
        if (parent() instanceof Term || parent() instanceof Exponent || parent() instanceof Rational) sb.append('(');
        List<Expression> terms = expression.getTerms();
        int max = terms.size() - 1;
        for (int idx = 0; idx <= max; idx++) {
            visit(terms.get(idx));
            if (idx != max) sb.append(" + ");
        }
        if (parent() instanceof Term || parent() instanceof Exponent || parent() instanceof Rational) sb.append(')');
        return null;
    }

    @Override
    public Void visit(Exponent expression) {
        if (parent() instanceof Term) sb.append('(');
        visit(expression.getBase());
        sb.append('^');
        visit(expression.getPower());
        if (parent() instanceof Term) sb.append(')');
        return null;
    }

    @Override
    public Void visit(Transcendental expression) {
        sb.append(expression.getName());
        return null;
    }

    @Override
    public Void visit(Trigonometric expression) {
        sb.append(expression.getName()).append('(');
        visit(expression.getArgument());
        sb.append(')');
        return null;
    }

    @Override
    public Void visit(Rational expression) {
        visit(expression.getNumerator());
        sb.append('/');
        visit(expression.getDenominator());
        return null;
    }

    private Expression parent() {
        return context.size() > 1 ?  context.get(context.size() - 2) : null;
    }

    @Override
    public Void visit(Constant<?> expression) {
        sb.append(expression.getValue());
        return null;
    }

    @Override
    public Void visit(Variable expression) {
        sb.append(expression.getName());
        return null;
    }
}

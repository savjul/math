package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;

import java.util.List;
import java.util.function.Function;

public final class RenderingVisitor extends TopDownVisitor<StringBuilder> {
    private static final RenderingVisitor VISITOR = new RenderingVisitor();
    private static final Function<Expression, String> INSTANCE = RenderingVisitor::render;

    public static Function<Expression, String> instance() {
        return INSTANCE;
    }

    private static String render(Expression expression) {
        StringBuilder sb = new StringBuilder();
        VISITOR.visit(sb, expression, null);
        return sb.toString();
    }

    @Override
    public void visit(StringBuilder sb, Expression expression, Expression parent) {
        if (!expression.isCompound()) {
            sb.append(expression.toString());
        }
        else {
            super.visit(sb, expression, parent);
        }
    }

    @Override
    public void visit(StringBuilder sb, Polynomial expression, Expression parent) {
        if (parent instanceof Term || parent instanceof Exponent || parent instanceof Rational) sb.append('(');
        List<Expression> terms = expression.getTerms();
        int max = terms.size() - 1;
        for (int idx = 0; idx <= max; idx++) {
            visit(sb, terms.get(idx), expression);
            if (idx != max) sb.append(" + ");
        }
        if (parent instanceof Term || parent instanceof Exponent || parent instanceof Rational) sb.append(')');
    }

    @Override
    public void visit(StringBuilder sb, Exponent expression, Expression parent) {
        if (parent instanceof Term) sb.append('(');
        visit(sb, expression.getBase(), expression);
        sb.append('^');
        visit(sb, expression.getPower(), expression);
        if (parent instanceof Term) sb.append(')');
    }

    @Override
    public void visit(StringBuilder sb, Rational expression, Expression parent) {
        visit(sb, expression.getNumerator(), expression);
        sb.append('/');
        visit(sb, expression.getDenominator(), expression);
    }
}

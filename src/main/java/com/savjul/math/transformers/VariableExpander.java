package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;
import com.savjul.math.expression.simple.IntegerConstant;
import com.savjul.math.expression.simple.Variable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class VariableExpander extends ExpressionVisitor<Expression> {
    private final Map<String, Expression> bindings;
    public static Builder get() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Expression> mapping;

        private Builder() {
            mapping = new LinkedHashMap<>();
        }

        public Builder add(String name, Expression value) {
            this.mapping.put(name, value);
            return this;
        }

        public Builder add(String name, int value) {
            this.mapping.put(name, IntegerConstant.of(value));
            return this;
        }

        public Function<Expression, Expression> build() {
            return expression -> expand(mapping, expression);
        }
    }


    private VariableExpander(Map<String, Expression> bindings) {
        this.bindings = bindings;
    }

    private static Expression expand(Map<String, Expression> bindings, Expression expression) {
        return new VariableExpander(bindings).visit(expression, null);
    }

    @Override
    public Expression visit(Expression expression, Expression parent) {
        if (expression instanceof Variable) {
            return bindings.getOrDefault(((Variable)expression).getName(), expression);
        }
        else {
            return super.visit(expression, parent);
        }
    }

    @Override
    public Expression visit(Term expression, Expression parent) {
        return Term.of(expression.getFactors().stream().map(e->visit(e, expression)));
    }

    @Override
    public Expression visit(Polynomial expression, Expression parent) {
        return Polynomial.of(expression.getTerms().stream().map(e->visit(e, expression)));
    }

    @Override
    public Expression visit(Exponent expression, Expression parent) {
        return Exponent.of(visit(expression.getBase(), expression), visit(expression.getPower(), expression));

    }

    @Override
    public Expression visit(Rational expression, Expression parent) {
        return Rational.of(visit(expression.getNumerator(), expression), visit(expression.getDenominator(), expression));
    }

    @Override
    public Expression defaultValue(Expression expression, Expression parent) {
        return expression;
    }
}

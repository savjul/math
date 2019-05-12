package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;
import com.savjul.math.expression.simple.IntegerConstant;
import com.savjul.math.expression.simple.Variable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class VariableExpander extends TopDownVisitor<Deque<Expression>> {
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
        Deque<Expression> state = new ArrayDeque<>();
        VariableExpander variableExpander = new VariableExpander(bindings);
        variableExpander.visit(state, expression, null);
        return state.removeLast();
    }

    @Override
    public void visit(Deque<Expression> state, Expression expression, Expression parent) {
        if (expression.isCompound()) {
            super.visit(state, expression, parent);
        }
        else {
            if (expression instanceof Variable) {
                state.addLast(bindings.getOrDefault(((Variable)expression).getName(), expression));
            }
            else {
                state.addLast(expression);
            }
        }
    }

    @Override
    public void visit(Deque<Expression> state, Term expression, Expression parent) {
        state.addLast(expression);
        super.visit(state, expression, parent);
        Deque<Expression> factors = new ArrayDeque<>(expression.getFactors().size());
        Expression e;
        while ((e = state.removeLast()) != expression) {
            factors.addFirst(e);
        }
        state.addLast(Term.of(factors));
    }

    @Override
    public void visit(Deque<Expression> state, Polynomial expression, Expression parent) {
        state.addLast(expression);
        super.visit(state, expression, parent);
        Deque<Expression> terms = new ArrayDeque<>(expression.getTerms().size());
        Expression e;
        while ((e = state.removeLast()) != expression) {
            terms.addFirst(e);
        }
        state.addLast(Polynomial.of(terms));
    }

    @Override
    public void visit(Deque<Expression> state, Exponent expression, Expression parent) {
        super.visit(state, expression, parent);
        Expression power = state.removeLast();
        Expression base = state.removeLast();
        state.addLast(Exponent.of(base, power));

    }

    @Override
    public void visit(Deque<Expression> state, Rational expression, Expression parent) {
        super.visit(state, expression, parent);
        Expression denominator = state.removeLast();
        Expression numerator = state.removeLast();
        state.addLast(Rational.of(numerator, denominator));
    }
}

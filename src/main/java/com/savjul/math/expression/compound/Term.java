package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.IntegerConstant;
import com.savjul.math.transformers.BasicComparison;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Term extends AbstractBaseExpression {
    private final List<Expression> factors;

    private Term(Stream<Expression> factors) {
        this.factors = factors.collect(Collectors.toList());
    }

    public static Expression of(Expression... factors) {
        return of(Stream.of(factors));
    }

    public static Term of(Stream<Expression> factors) {
        return new Term(factors);
    }

    public static Expression of(Collection<Expression> factors) {
        return new Term(factors.stream());
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return this.factors.stream().allMatch(Expression::isConstant);
    }

    public List<Expression> getFactors() {
        return this.factors;
    }

    public List<Expression> getFactors(boolean constant) {
        return this.factors.stream().filter(e->e.isConstant() == constant).collect(Collectors.toList());
    }

    public static List<Expression> getFactors(Expression expression) {
        return expression instanceof Term ? ((Term) expression).getFactors() : Collections.singletonList(expression);
    }

    public static List<Expression> getFactors(Expression expression, boolean constant) {
        return expression instanceof Term ? ((Term) expression).getFactors(constant) :
                expression.isConstant() == constant ? Collections.singletonList(expression) : Collections.emptyList();

    }

    public static Expression getConstantCoefficient(Expression expression) {
        if (expression instanceof Term) {
            List<Expression> factors = ((Term) expression).getFactors(true);
            return factors.size() == 1 ? factors.get(0) : of(factors.stream().sorted(BasicComparison.factors()));
        } else if (expression.isConstant()) {
            return expression;
        } else {
            return IntegerConstant.ONE;
        }
    }

    public static List<Expression> getNonConstants(Expression expression) {
        if (expression instanceof Term) {
            return ((Term)expression).getFactors(false);
        } else if (expression.isConstant()) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(expression);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        return factors.equals(term.factors);
    }

    @Override
    public int hashCode() {
        return factors.hashCode();
    }
}

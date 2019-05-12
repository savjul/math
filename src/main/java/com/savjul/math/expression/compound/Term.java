package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;

import java.util.Collection;
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

    public static Expression of(Stream<Expression> factors) {
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

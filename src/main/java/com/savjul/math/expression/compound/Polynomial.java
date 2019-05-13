package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Polynomial extends AbstractBaseExpression {
    private final List<Expression> terms;

    private Polynomial(Stream<Expression> terms) {
        this.terms = terms.collect(Collectors.toList());
    }

    public static Polynomial of(Expression... terms) {
        return of(Stream.of(terms));
    }

    public static Polynomial of(Collection<Expression> terms) {
        return new Polynomial(terms.stream());
    }

    public static Polynomial of(Stream<Expression> terms) {
        return new Polynomial(terms);
    }

    @Override
    public Expression times(Expression o) {
        return super.times(o);
    }

    @Override
    public boolean isConstant() {
        return this.terms.stream().allMatch(Expression::isConstant);
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    public List<Expression> getTerms() {
        return this.terms;
    }

    public static List<Expression> getTerms(Expression expression) {
        return expression instanceof Polynomial ? ((Polynomial) expression).getTerms() : Collections.singletonList(expression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polynomial that = (Polynomial) o;

        return terms.equals(that.terms);
    }

    @Override
    public int hashCode() {
        return terms.hashCode();
    }
}

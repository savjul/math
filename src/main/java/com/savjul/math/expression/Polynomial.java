package com.savjul.math.expression;

import java.util.Collection;
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
    public Expression withContext(Context context) {
        return new Polynomial(
                this.terms.stream().map(e->e.withContext(context)));
    }

    @Override
    public Expression times(Expression o) {
        return super.times(o);
    }

    @Override
    public boolean isConstant() {
        return this.terms.stream().allMatch(Expression::isConstant);
    }


    public List<Expression> getTerms() {
        return this.terms;
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

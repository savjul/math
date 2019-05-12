package com.savjul.math.expression;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Polynomial extends AbstractBaseExpression {
    private final List<Expression> terms;

    private Polynomial(Expression parent, Stream<Expression> terms) {
        super(parent);
        this.terms = terms.map(t->t.withParent(this)).collect(Collectors.toList());
    }

    public static Polynomial of(Expression... terms) {
        return of(Stream.of(terms));
    }

    public static Polynomial of(Collection<Expression> terms) {
        return new Polynomial(null, terms.stream());
    }

    public static Polynomial of(Stream<Expression> terms) {
        return new Polynomial(null, terms);
    }

    public Polynomial withParent(Expression parent) {
        return new Polynomial(parent, this.terms.stream());
    }

    @Override
    public Expression withContext(Context context) {
        return new Polynomial(null,
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
    public String render() {
        String result = String.join(" + ", terms.stream().map(Expression::render).collect(Collectors.toList()));
        return this.getParent() != null ? "(" + result + ")" : result;
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

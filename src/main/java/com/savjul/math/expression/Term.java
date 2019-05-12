package com.savjul.math.expression;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Term extends AbstractBaseExpression {
    private final List<Expression> factors;

    private Term(Expression parent, Stream<Expression> factors) {
        super(parent);
        this.factors = factors.map(f->f.withParent(this)).collect(Collectors.toList());
    }

    public static Expression of(Expression... factors) {
        return of(Stream.of(factors));
    }

    public static Expression of(Stream<Expression> factors) {
        return new Term(null, factors);
    }

    public static Expression of(Collection<Expression> factors) {
        return new Term(null, factors.stream());
    }

    @Override
    public Term withParent(Expression parent) {
        return new Term(parent, this.factors.stream());
    }

    @Override
    public Expression withContext(Context context) {
        return new Term(null, this.factors.stream().map(f->f.withContext(context)));
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
    public String render() {
        return String.join("", factors.stream().map(Expression::render).collect(Collectors.toList()));
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

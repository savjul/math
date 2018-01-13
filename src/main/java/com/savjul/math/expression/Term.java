package com.savjul.math.expression;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Term extends Expression {
    private final List<Expression> factors;

    private Term(Expression parent, List<Expression> factors) {
        super(parent);
        this.factors = new ArrayList<>();
        for (Expression factor: factors) {
            if (factor instanceof Term) {
                this.factors.addAll(((Term) factor).getFactors().stream().map(f->f.of(this)).collect(Collectors.toList()));
            }
            else {
                this.factors.add(factor.of(this));
            }
        }
        this.factors.sort(Comparator.naturalOrder());
    }

    public static Term of(Expression... factors) {
        return new Term(null, Arrays.asList(factors));
    }

    @Override
    public Term of(Expression parent) {
        return new Term(parent, this.factors);
    }

    @Override
    public Expression add(Expression o) {
        if (o instanceof Term) {
            Term other = (Term) o;
            if (other.getNonConstantExpression().equals(this.getNonConstantExpression())) {
                return Term.of(this.getConstantExpression().add(other.getConstantExpression()), this.getNonConstantExpression());
            }
        }
        return Polynomial.of(this, o);
    }

    @Override
    public Expression multiply(Expression o) {
        if (o instanceof Term) {
            Term other = (Term) o;
            List<Expression> all = new ArrayList<>(this.factors.size() + other.factors.size());
            all.addAll(this.factors);
            all.addAll(other.factors);
            return new Term(null, all);
        }
        else {
            return Term.of(this, o);
        }
    }

    @Override
    public Expression simplify() {
        Deque<Expression> factors = new ArrayDeque<>(this.factors);
        List<Expression> result = new ArrayList<>(this.factors.size());
        while (! factors.isEmpty()) {
            Expression current = factors.pollFirst();
            while (! factors.isEmpty() && current.getClass().isInstance(factors.peekFirst())) {
                current = current.multiply(factors.pollFirst());
            }
            result.add(current.simplify());
        }
        if (result.stream().filter(f->f instanceof IntegerConstant).map(f->(IntegerConstant)f).anyMatch(f->f.getValue() == 0)) {
            result = Collections.singletonList(IntegerConstant.of(0));
        }
        return result.size() == 1 ? result.get(0) : new Term(null, result);
    }

    public List<Expression> getFactors() {
        return this.factors;
    }

    public Expression getConstantExpression() {
        return this.getFilteredTerm(e->e instanceof IntegerConstant).simplify();
    }

    public Expression getNonConstantExpression() {
        return this.getFilteredTerm(e->! (e instanceof IntegerConstant)).simplify();
    }

    private Term getFilteredTerm(Predicate<Expression> f) {
        return new Term(null, this.factors.stream().filter(f).collect(Collectors.toList()));
    }

    @Override
    public int order() {
        return ExpressionConstants.TERM_ORDER;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Term) {
            return compare(this.factors, ((Term) o).factors);
        }
        return super.compareTo(o);
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

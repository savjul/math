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
                this.factors.addAll(((Term) factor).getFactors().stream().map(f->f.withParent(this)).collect(Collectors.toList()));
            }
            else {
                this.factors.add(factor.withParent(this));
            }
        }
        this.factors.sort(Comparator.naturalOrder());
    }

    public static Term of(Expression... factors) {
        return new Term(null, Arrays.asList(factors));
    }

    @Override
    public Term withParent(Expression parent) {
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
    public Term simplify() {
        Deque<Expression> factors = new ArrayDeque<>(this.factors);
        List<Expression> result = new ArrayList<>(this.factors.size());
        while (! factors.isEmpty()) {
            Expression current = factors.pollFirst();
            while (! factors.isEmpty() && current.getClass().isInstance(factors.peekFirst())) {
                current = current.multiply(factors.pollFirst());
            }
            // to prevent infinite recursion, you must prevent infinite recursion
            if (! current.equals(this)) {
                current = current.simplify();
            }
            if (! isOne(current)) {
                result.add(current);
            }
        }
        if (result.stream().filter(f->f instanceof IntegerConstant).map(f->(IntegerConstant)f).anyMatch(f->f.getValue() == 0)) {
            result = Collections.singletonList(IntegerConstant.of(0));
        }
        if (result.size() == 0) {
            result = Collections.singletonList(IntegerConstant.of(1));
        }
        return new Term(null, result);
    }

    public List<Expression> getFactors() {
        return this.factors;
    }

    public IntegerConstant getConstantExpression() {
        return this.factors.stream().filter(e->e instanceof IntegerConstant)
                .map(e->((IntegerConstant) e))
                .findFirst().orElse(IntegerConstant.of(1));
    }

    public Term getNonConstantExpression() {
        return this.getFilteredTerm(e->! (e instanceof IntegerConstant));
    }

    private Term getFilteredTerm(Predicate<Expression> f) {
        return new Term(null, this.factors.stream().filter(f).collect(Collectors.toList()));
    }

    private boolean isOne(Expression e) {
        return e instanceof IntegerConstant && ((IntegerConstant) e).getValue() == 1;
    }

    @Override
    public int order() {
        return this.getNonConstantExpression().factors.stream().map(Expression::order)
                .max(Comparator.naturalOrder()).orElse(ExpressionConstants.INTEGER_ORDER_OTHER);
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

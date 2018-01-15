package com.savjul.math.expression;

import java.util.*;
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
    public Expression withContext(Context context) {
        return new Term(null, this.factors.stream().map(f->f.withContext(context)).collect(Collectors.toList()));
    }

    @Override
    public Expression plus(Expression o) {
        return Polynomial.of(this, o);
    }

    @Override
    public Expression times(Expression o) {
        if (o instanceof Term) {
            Term other = (Term) o;
            List<Expression> all = new ArrayList<>(this.factors.size() + other.factors.size());
            all.addAll(this.factors);
            all.addAll(other.factors);
            return new Term(null, all);
        }
        else {
            List<Expression> all = new ArrayList<>();
            for (Expression e: this.factors) {
                if (o != null && e.getBase().equals(o.getBase())) {
                    all.add(e.times(o));
                    o = null;
                }
                else {
                    all.add(e);
                }
            }
            if (o == null) {
                return new Term(null, all);
            }
            else {
                return super.times(o);
            }
        }
    }

    @Override
    public Expression simplify() {
        Deque<Expression> factors = new ArrayDeque<>(this.factors);
        List<Expression> result = new ArrayList<>(this.factors.size());
        while (! factors.isEmpty()) {
            Expression current = factors.pollFirst();
            while (! factors.isEmpty() && current.getClass().isInstance(factors.peekFirst())) {
                current = current.times(factors.pollFirst());
            }
            while (! factors.isEmpty() && factors.peekFirst() instanceof Polynomial) {
                result.add(current);
                Term factor = new Term(null, result);
                Polynomial polynomial = (Polynomial) factors.pollFirst();
                current = Polynomial.multiply(factor, polynomial);
                result = new ArrayList<>();
            }
            if (! current.equals(this)) {
                current = current.simplify();
            }
            if (! isOne(current)) {
                result.add(current);
            }
        }
        if (result.stream().filter(f->f instanceof IntegerConstant).map(f->(IntegerConstant)f).anyMatch(f->f.getValue() == 0)) {
            result = Collections.singletonList(IntegerConstant.ZERO);
        }
        return result.size() == 0 ? IntegerConstant.ONE : result.size() == 1 ? result.get(0) :
                new Term(null, result);
    }

    public List<Expression> getFactors() {
        return this.factors;
    }

    @Override
    public Expression getCoefficient() {
        return this.factors.stream().filter(f->f instanceof IntegerConstant)
                .reduce(IntegerConstant.ONE, Expression::times);
    }

    @Override
    public List<Expression> getNonCoefficients() {
        return this.factors.stream().filter(f->! (f instanceof IntegerConstant)).collect(Collectors.toList());
    }

    private boolean isOne(Expression e) {
        return IntegerConstant.ONE.equals(e);
    }

    @Override
    public int order() {
        return this.getNonCoefficients().stream().map(Expression::order)
                .max(Comparator.naturalOrder()).orElse(ExpressionConstants.INTEGER_ORDER_OTHER);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Term) {
            int result = compare(this.getNonCoefficients(), o.getNonCoefficients());
            if (result == 0) {
                Expression thisCo = this.getCoefficient();
                Expression otherCo = o.getCoefficient();
                return thisCo.compareTo(otherCo);
            }
            else {
                return result;
            }
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

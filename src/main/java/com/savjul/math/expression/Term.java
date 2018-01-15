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

    private static Expression term(List<Expression> factors) {
        factors = factors.stream().filter(f->!IntegerConstant.ONE.equals(f)).collect(Collectors.toList());
        return factors.size() == 0 ? IntegerConstant.ONE : factors.size() == 1 ? factors.get(0) :
                new Term(null, factors);

    }

    @Override
    public Expression times(Expression o) {
        if (o.equals(IntegerConstant.ONE)) {
            return this;
        }
        else if (o.equals(IntegerConstant.ZERO)) {
            return IntegerConstant.ZERO;
        }
        List<Expression> ofactors = o instanceof Term ? ((Term) o).factors : Collections.singletonList(o);
        List<Expression> factors = new ArrayList<>(this.factors.size() + ofactors.size());
        factors.addAll(this.factors);
        factors.addAll(ofactors);
        factors.sort(Comparator.naturalOrder());
        Expression previous = IntegerConstant.ONE;
        List<Expression> result = new ArrayList<>(factors.size());
        for (Expression e: factors) {
            if (e.getBase().equals(previous.getBase())) {
                result.add(e.times(previous));
                e = IntegerConstant.ONE;
            }
            else if (! IntegerConstant.ONE.equals(previous)) {
                result.add(previous);
            }
            previous = e;
        }
        if (! IntegerConstant.ONE.equals(previous)) {
            result.add(previous);
        }
        return term(result);
    }

    @Override
    public Expression simplify() {
        return this.factors.stream().map(Expression::simplify).reduce(IntegerConstant.ONE, Polynomial::multiply);
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

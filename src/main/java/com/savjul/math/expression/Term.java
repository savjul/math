package com.savjul.math.expression;

import java.util.*;
import java.util.stream.Collectors;

public final class Term extends AbstractBaseExpression {
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

    public static Expression of(Expression... factors) {
        return term(Arrays.asList(factors));
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
        return combine(factors, l->new Term(null, l), IntegerConstant.ONE);
    }

    @Override
    public Expression times(Expression o) {
        return this.applyOp(Expression::times, o,
                (e) -> e instanceof Term ? ((Term) e).factors : Collections.singletonList(e),
                (e1, e2) -> e1.getBase().equals(e2.getBase()),
                Term::term);
    }

    @Override
    public boolean isConstant() {
        return this.factors.stream().allMatch(Expression::isConstant);
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
        return this.factors.stream().filter(Expression::isConstant)
                .reduce(IntegerConstant.ONE, Expression::times);
    }

    @Override
    public List<Expression> getNonCoefficients() {
        return this.factors.stream().filter(f->! (f.isConstant())).collect(Collectors.toList());
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

package com.savjul.math.expression;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Term extends AbstractBaseExpression {
    private final List<Expression> factors;

    private Term(Expression parent, Stream<Expression> factors) {
        super(parent);
        this.factors = factors.map(f->f.withParent(this)).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
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

    private static List<Expression> getConstantPortion(Expression e) {
        if (e instanceof Term) {
            return ((Term) e).factors.stream().filter(Expression::isConstant).collect(Collectors.toList());
        }
        else {
            if (e.isConstant()) {
                return Collections.singletonList(e);
            }
            else {
                return Collections.emptyList();
            }
        }
    }

    private static List<Expression> getNonConstantPortion(Expression e) {
        if (e instanceof Term) {
            return ((Term) e).factors.stream().filter(f->! (f.isConstant())).collect(Collectors.toList());
        }
        else {
            if (!e.isConstant()) {
                return Collections.singletonList(e);
            }
            else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public int order() {
        return getNonConstantPortion(this).stream().map(Expression::order)
                .max(Comparator.naturalOrder()).orElse(ExpressionConstants.INTEGER_ORDER_OTHER);
    }

    @Override
    public int compareTo(Expression o) {
        List<Expression> otherNonContantPortion = getNonConstantPortion(o);
        if (!otherNonContantPortion.isEmpty()) {
            int result = compare(getNonConstantPortion(this), otherNonContantPortion);
            if (result == 0) {
                return compare(getConstantPortion(this), getConstantPortion(o));
            }
            else {
                return result;
            }
        }
        else {
            return super.compareTo(o);
        }
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

package com.savjul.math.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractBaseExpression implements Expression {
    private final Expression parent;

    protected AbstractBaseExpression(Expression parent) {
        this.parent = parent;
    }

    @Override
    public Expression withContext(Context context) {
        return this;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Expression getCoefficient() {
        return IntegerConstant.ONE;
    }

    @Override
    public List<Expression> getNonCoefficients() {
        return Collections.singletonList(this);
    }

    @Override
    public Expression getBase() {
        return this;
    }

    @Override
    public Expression getPower() {
        return IntegerConstant.ONE;
    }

    @Override
    public Expression plus(Expression o) {
        if (o instanceof Polynomial) {
            return o.plus(this);
        }
        else if (this.getNonCoefficients().equals(o.getNonCoefficients())) {
            Expression c = this.getCoefficient().plus(o.getCoefficient());
            return this.getNonCoefficients().stream().reduce(c, Expression::times);
        }
        return Polynomial.of(this, o);
    }

    @Override
    public Expression times(Expression o) {
        if (o instanceof Term) {
            return o.times(this);
        }
        else if (o instanceof Rational) {
            return o.times(this);
        }
        else if (this.getBase().equals(o.getBase())) {
            return Exponent.of(this.getBase(), this.getPower().plus(o.getPower()));
        }
        return Term.of(this, o);
    }

    @Override
    public Expression divideBy(Expression o) {
        return Rational.of(this, o);
    }

    @Override
    public Expression invert() {
        return Rational.of(IntegerConstant.ONE, this);
    }

    @Override
    public Expression pow(Expression o) {
        return Exponent.of(this, o);
    }

    @Override
    public Expression simplify() {
        return this;
    }

    @Override
    public Expression getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return this.render();
    }

    @Override
    public int compareTo(Expression o) {
        return Integer.compare(this.order(), o.order());
    }

    // functions used by Term and Polynomial
    static Expression combine(List<Expression> expressions, Function<List<Expression>, Expression> combiner, Expression identity) {
        expressions = expressions.stream().filter(e->!e.equals(identity)).collect(Collectors.toList());
        return expressions.size() == 0 ? identity : expressions.size() == 1 ? expressions.get(0) :  combiner.apply(expressions);
    }

    Expression applyOp(BiFunction<Expression, Expression, Expression> operand, Expression other,
                       Function<Expression, List<Expression>> getContents,
                       BiPredicate<Expression, Expression> combineIf,
                       Function<List<Expression>, Expression> createExpression) {
        List<Expression> items = new ArrayList<>(getContents.apply(this));
        items.addAll(getContents.apply(other));
        Collections.sort(items);
        List<Expression> result = new ArrayList<>(items.size());
        for (Expression e: items) {
            if (! result.isEmpty() && combineIf.test(result.get(result.size()-1), e)) {
                e = operand.apply(result.remove(result.size()-1), e);
            }
            result.add(e);
        }
        return createExpression.apply(result);
    }

    static int compare(List<? extends Expression> l1, List<? extends Expression> l2) {
        int min = Math.min(l1.size(), l2.size());
        for (int idx = 0; idx < min; idx++) {
            int res = l1.get(idx).compareTo(l2.get(idx));
            if (res != 0) {
                return res;
            }
        }
        return Integer.compare(l1.size(), l2.size());
    }
}
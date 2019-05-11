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
    public Expression plus(Expression o) {
        return Polynomial.of(this, o);
    }

    @Override
    public Expression times(Expression o) {
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
        return BasicSimplifier.instance().simplify(this);
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

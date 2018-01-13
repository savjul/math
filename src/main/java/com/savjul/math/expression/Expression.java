package com.savjul.math.expression;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public abstract class Expression implements Comparable<Expression> {
    private final Expression parent;

    protected Expression(Expression parent) {
        this.parent = parent;
    }

    public abstract Expression of(Expression parent);


    public abstract String render();

    public int order() {
        return Integer.MAX_VALUE;
    }

    public Expression add(Expression other) {
        return Polynomial.of(this, other);
    }

    public Expression multiply(Expression other) {
        return Term.of(this, other);
    }

    public Expression exp(Expression other) {
        return Exponent.of(this, other);
    }

    public Expression simplify() {
        return this;
    }

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

    protected static int compare(List<Expression> l1, List<Expression> l2) {
        Deque<Expression> q1 = new ArrayDeque<>(l1);
        Deque<Expression> q2 = new ArrayDeque<>(l2);
        while (! (q1.isEmpty() || q2.isEmpty())) {
            Expression te = q1.pollFirst();
            Expression oe = q2.pollFirst();
            int result = te.compareTo(oe);
            if (result != 0) {
                return result;
            }
        }
        if (q1.isEmpty() && q2.isEmpty()) {
            return 0;
        }
        else if (q1.isEmpty()) {
            return -1;
        }
        return 1;

    }
}

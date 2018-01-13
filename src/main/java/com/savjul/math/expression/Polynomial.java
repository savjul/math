package com.savjul.math.expression;

import java.util.*;
import java.util.stream.Collectors;

public final class Polynomial extends Expression {
    private final List<Expression> terms;

    private Polynomial(Expression parent, List<Expression> terms) {
        super(parent);
        this.terms = new ArrayList<>();
        for (Expression term: terms) {
            if (term instanceof Polynomial) {
                this.terms.addAll(((Polynomial) term).getTerms().stream().map(t->t.of(this)).collect(Collectors.toList()));
            }
            else {
                this.terms.add(term.of(this));
            }
        }
        this.terms.sort(Comparator.naturalOrder());
    }

    public static Polynomial of(Expression... terms) {
        return new Polynomial(null, Arrays.asList(terms));
    }

    public Polynomial of(Expression parent) {
        return new Polynomial(parent, this.terms);
    }

    public Expression add(Expression o) {
        if (o instanceof Polynomial) {
            Polynomial other = (Polynomial) o;
            List<Expression> all = new ArrayList<>(this.terms.size() + other.terms.size());
            all.addAll(this.terms);
            all.addAll(other.terms);
            return new Polynomial(null, all);
        }
        else if (o instanceof Term) {
            Term other = (Term) o;
            List<Expression> all = new ArrayList<>();
            for (Expression e: this.terms) {
                if (other != null && e instanceof Term && ((Term) e).getNonConstantExpression().equals(other.getNonConstantExpression())) {
                    all.add(o.add(e));
                    other = null;
                }
                else {
                    all.add(e);
                }
            }
            if (other != null) {
                all.add(other);
            }
            return new Polynomial(null, all);
        }
        else {
            List<Expression> all = new ArrayList<>(this.terms.size() + 1);
            all.addAll(this.terms);
            all.add(o);
            return new Polynomial(null, all);
        }
    }

    @Override
    public Expression multiply(Expression other) {
        return Term.of(this, other);
    }

    public static Polynomial multiply(Expression e, Polynomial p) {
        List<Expression> result = new ArrayList<>();
        for (Expression part: p.getTerms()) {
            result.add(e.multiply(part));
        }
        return new Polynomial(null, result);
    }

    public static Polynomial multiply(Polynomial p1, Polynomial p2) {
        List<Expression> result = new ArrayList<>();
        for (Expression part: p1.getTerms()) {
            result.add(multiply(part, p2));
        }
        return new Polynomial(null, result);
    }

    @Override
    public Expression simplify() {
        Deque<Expression> terms = new ArrayDeque<>(this.terms);
        List<Expression> result = new ArrayList<>(this.terms.size());
        while (! terms.isEmpty()) {
            Expression current = terms.pollFirst();
            while (! terms.isEmpty() && current.getClass().isInstance(terms.peekFirst())) {
                current = current.add(terms.pollFirst()).simplify();
            }
            if (! (current instanceof IntegerConstant) || ((IntegerConstant) current).getValue() != 0) {
                result.add(current.simplify());
            }
        }
        return result.size() == 1 ? result.get(0) : new Polynomial(null, result);
    }

    @Override
    public int order() {
        return ExpressionConstants.POLYNOMIAL_ORDER;
    }

    public List<Expression> getTerms() {
        return this.terms;
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Polynomial) {
            return compare(this.terms, ((Polynomial) o).terms);
        }
        return super.compareTo(o);
    }

    @Override
    public String render() {
        String result = String.join(" + ", terms.stream().map(Expression::render).collect(Collectors.toList()));
        return this.getParent() != null ? "(" + result + ")" : result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polynomial that = (Polynomial) o;

        return terms.equals(that.terms);
    }

    @Override
    public int hashCode() {
        return terms.hashCode();
    }
}

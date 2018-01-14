package com.savjul.math.expression;

import java.util.*;
import java.util.stream.Collectors;

public final class Polynomial extends Expression {
    private final List<Term> terms;

    private Polynomial(Expression parent, List<? extends Expression> terms) {
        super(parent);
        this.terms = new ArrayList<>();
        for (Expression term: terms) {
            if (term instanceof Polynomial) {
                this.terms.addAll(((Polynomial) term).getTerms().stream().map(t->t.withParent(this)).collect(Collectors.toList()));
            }
            else if (term instanceof Term) {
                this.terms.add((Term)term.withParent(this));
            }
            else {
                this.terms.add(Term.of(term));
            }
        }
        this.terms.sort(Comparator.naturalOrder());
    }

    public static Polynomial of(Expression... terms) {
        return new Polynomial(null, Arrays.asList(terms));
    }

    public Polynomial withParent(Expression parent) {
        return new Polynomial(parent, this.terms);
    }

    public Expression add(Expression o) {
        if (o instanceof Polynomial) {
            Polynomial other = (Polynomial) o;
            List<Term> all = new ArrayList<>(this.terms.size() + other.terms.size());
            all.addAll(this.terms);
            all.addAll(other.terms);
            return new Polynomial(null, all);
        }
        else if (o instanceof Term) {
            Term other = (Term) o;
            List<Term> all = new ArrayList<>();
            for (Term e: this.terms) {
                if (other != null && e.getNonConstantExpression().equals(other.getNonConstantExpression())) {
                    Term sum = Term.of(e.getConstantExpression().add(other.getConstantExpression()), e.getNonConstantExpression());
                    all.add(sum);
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
            result.add(e.multiply(part).simplify());
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
        Deque<Term> terms = new ArrayDeque<>(this.terms);
        List<Term> result = new ArrayList<>(this.terms.size());
        while (! terms.isEmpty()) {
            Term current = terms.pollFirst();
            while (! terms.isEmpty() && current.getNonConstantExpression().equals(terms.peekFirst().getNonConstantExpression())) {
                Term other = terms.pollFirst();
                IntegerConstant c = current.getConstantExpression().add(other.getConstantExpression());
                current = Term.of(c, current.getNonConstantExpression()).simplify();
            }
            result.add(current);
        }
        return result.size() == 1 ? result.get(0) : new Polynomial(null, result);
    }

    @Override
    public int order() {
        return ExpressionConstants.POLYNOMIAL_ORDER;
    }

    public List<Term> getTerms() {
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

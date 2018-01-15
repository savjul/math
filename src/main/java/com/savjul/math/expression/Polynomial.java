package com.savjul.math.expression;

import java.util.*;
import java.util.stream.Collectors;

public final class Polynomial extends Expression {
    private final List<Expression> terms;

    private Polynomial(Expression parent, List<? extends Expression> terms) {
        super(parent);
        this.terms = new ArrayList<>();
        if (terms.stream().anyMatch(IntegerConstant.ZERO::equals)) {
            int x = 1 + 1;
        }
        for (Expression term: terms) {
            if (term instanceof Polynomial) {
                this.terms.addAll(((Polynomial) term).getTerms().stream().map(t->t.withParent(this)).collect(Collectors.toList()));
            }
            else {
                this.terms.add(term.withParent(this));
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

    @Override
    public Expression withContext(Context context) {
        return new Polynomial(null,
                this.terms.stream().map(e->e.withContext(context)).collect(Collectors.toList()));
    }

    private static Expression polynomial(List<Expression> terms) {
        terms = terms.stream().filter(t->!t.equals(IntegerConstant.ZERO)).collect(Collectors.toList());
        return terms.size() == 0 ? IntegerConstant.ZERO :
                terms.size() == 1 ? terms.get(0) :
                        new Polynomial(null, terms);
    }

    public Expression plus(Expression o) {
        if (o.equals(IntegerConstant.ZERO)) {
            return this;
        }
        List<Expression> oterms = o instanceof Polynomial ? ((Polynomial) o).terms : Collections.singletonList(o);
        List<Expression> terms = new ArrayList<>(this.terms.size() + oterms.size());
        terms.addAll(this.terms);
        terms.addAll(oterms);
        terms.sort(Comparator.naturalOrder());
        Expression previous = IntegerConstant.ZERO;
        List<Expression> result = new ArrayList<>(terms.size());
        for (Expression e: terms) {
            if (e.getNonCoefficients().equals(previous.getNonCoefficients())) {
                result.add(e.plus(previous));
                e = IntegerConstant.ZERO;
            }
            else if (! IntegerConstant.ZERO.equals(previous)) {
                result.add(previous);
            }
            previous = e;
        }
        if (! IntegerConstant.ZERO.equals(previous)) {
            result.add(previous);
        }
        return polynomial(result);
    }

    @Override
    public Expression times(Expression o) {
        return super.times(o);
    }

    public static Expression multiply(Expression e1, Expression e2) {
        if (e1 instanceof Polynomial && e2 instanceof Polynomial) {
            return multiply((Polynomial) e1, (Polynomial) e2);
        }
        else if (e1 instanceof Polynomial) {
            return multiply(e2, (Polynomial) e1);
        }
        else if (e2 instanceof Polynomial) {
            return multiply(e1, (Polynomial) e2);
        }
        return e1.times(e2);
    }

    public static Polynomial multiply(Expression e, Polynomial p) {
        List<Expression> result = new ArrayList<>();
        for (Expression part: p.getTerms()) {
            result.add(e.times(part));
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
        return this.terms.stream().map(Expression::simplify).reduce(IntegerConstant.ZERO, Expression::plus);
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

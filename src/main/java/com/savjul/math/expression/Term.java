package com.savjul.math.expression;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Expression add(Expression o) {
        if (o instanceof Term) {
            Term other = (Term) o;
            if (other.getNonConstantExpression().equals(this.getNonConstantExpression())) {
                Optional<Expression> nonConstant = this.getNonConstantExpression();
                Expression result = Term.of(this.getConstantExpression().orElse(IntegerConstant.ONE)
                        .add(other.getConstantExpression().orElse(IntegerConstant.ONE)));
                if (nonConstant.isPresent()) {
                    result = result.multiply(nonConstant.get());
                }
                return result;
            }
        }
        return Polynomial.of(this, o);
    }

    @Override
    public Expression multiply(Expression o) {
        if (o instanceof Term) {
            Term other = (Term) o;
            List<Expression> all = new ArrayList<>(this.factors.size() + other.factors.size());
            all.addAll(this.factors);
            all.addAll(other.factors);
            return new Term(null, all);
        }
        else {
            return super.multiply(o);
        }
    }

    @Override
    public Expression simplify() {
        Deque<Expression> factors = new ArrayDeque<>(this.factors);
        List<Expression> result = new ArrayList<>(this.factors.size());
        while (! factors.isEmpty()) {
            Expression current = factors.pollFirst();
            while (! factors.isEmpty() && current.getClass().isInstance(factors.peekFirst())) {
                current = current.multiply(factors.pollFirst());
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

    public static Optional<IntegerConstant> getCoefficient(Expression expression) {
        if (expression instanceof Term) {
            return ((Term) expression).getConstantExpression();
        }
        else if (expression instanceof IntegerConstant) {
            return Optional.of((IntegerConstant)expression);
        }
        else {
            return Optional.empty();
        }
    }

    public static Optional<Expression> getNonCoefficient(Expression expression) {
        if (expression instanceof Term) {
            return ((Term) expression).getNonConstantExpression();
        }
        else if (expression instanceof IntegerConstant) {
            return Optional.empty();
        }
        else {
            return Optional.of(expression);
        }
    }

    private Optional<IntegerConstant> getConstantExpression() {
        return this.factors.stream().filter(e->e instanceof IntegerConstant).map(e->(IntegerConstant)e)
                .findFirst();
    }

    private Optional<Expression> getNonConstantExpression() {
        List<Expression> nonConstants = getNonConstants().collect(Collectors.toList());
        return nonConstants.size() == 0 ? Optional.empty() :
                Optional.of(nonConstants.size() == 1 ? nonConstants.get(0) : new Term(null, nonConstants));
    }

    private Stream<Expression> getNonConstants() {
        return this.factors.stream().filter(e->!(e instanceof IntegerConstant));
    }

    private boolean isOne(Expression e) {
        return IntegerConstant.ONE.equals(e);
    }

    @Override
    public int order() {
        return this.getNonConstants().map(Expression::order)
                .max(Comparator.naturalOrder()).orElse(ExpressionConstants.INTEGER_ORDER_OTHER);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Term) {
            int result = compare(this.getNonConstants().collect(Collectors.toList()),
                    ((Term) o).getNonConstants().collect(Collectors.toList()));
            if (result == 0) {
                Optional<IntegerConstant> thisCo = this.getConstantExpression();
                Optional<IntegerConstant> otherCo = ((Term) o).getConstantExpression();
                return ! thisCo.isPresent() ? 1 : ! otherCo.isPresent() ? -1 : thisCo.get().compareTo(otherCo.get());
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

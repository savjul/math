package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.*;
import com.savjul.math.expression.simple.Constant;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExpressionSimplifier extends ExpressionVisitor<Expression> {
    private static final ExpressionSimplifier SIMPLIFIER = new ExpressionSimplifier();
    private static final Function<Expression, Expression> INSTANCE = ExpressionSimplifier::expand;

    public static Function<Expression, Expression> instance() { return INSTANCE; }

    private static Expression expand(Expression expression) {
        return SIMPLIFIER.visit(expression, null);
    }

    @Override
    public Expression visit(Expression expression, Expression parent) {
        if (expression.isCompound()) {
            return super.visit(expression, parent);
        }
        else {
            return expression;
        }
    }

    @Override
    public Expression visit(Term expression, Expression parent) {
        PriorityQueue<Expression> factors = new PriorityQueue<>(BasicComparison.termSimplify());
        factors.addAll(expression.getFactors().stream().map(e->visit(e, expression))
                .flatMap(e->e instanceof Term ? ((Term) e).getFactors().stream() : Stream.of(e))
                .collect(Collectors.toList()));
        Deque<Expression> result = new ArrayDeque<>();
        while (!factors.isEmpty()) {
            Expression e1 = factors.remove();
            if (isZero(e1)) {
                result.clear(); factors.clear(); result.add(Constant.ZERO);
            }
            else if (isOne(e1)) {
            }
            else if (e1 instanceof Term) {
                factors.addAll(((Term) e1).getFactors());
            }
            else if (result.isEmpty() ){
                result.addLast(e1);
            }
            else {
                Expression e2 = result.removeLast();
                if (e1 instanceof Constant && e2 instanceof Constant) {
                    factors.add(multiply((Constant<?>) e1, (Constant<?>) e2));
                }
                else if (e1 instanceof Rational && e2 instanceof Rational) {
                    Expression e1num = ((Rational) e1).getNumerator();
                    Expression e1den = ((Rational) e1).getDenominator();
                    Expression e2num = ((Rational) e2).getNumerator();
                    Expression e2den = ((Rational) e2).getDenominator();
                    Rational newRational = Rational.of(e1num.times(e2num), e1den.times(e2den));
                    factors.add(visit(newRational, parent));
                }
                else if (e1 instanceof Rational) {
                    Expression newNumerator = ((Rational) e1).getNumerator().times(e2);
                    Expression newDenominator = ((Rational) e1).getDenominator();
                    Rational newRational = Rational.of(newNumerator, newDenominator);
                    factors.add(visit(newRational, parent));
                }
                else if (e2 instanceof Rational) {
                    Expression newNumerator = ((Rational) e2).getNumerator().times(e1);
                    Expression newDenominator = ((Rational) e2).getDenominator();
                    Rational newRational = Rational.of(newNumerator, newDenominator);
                    factors.add(visit(newRational, parent));
                }
                else if (Exponent.getBase(e1).equals(Exponent.getBase(e2))) {
                    Exponent newExponent = Exponent.of(Exponent.getBase(e1), Exponent.getPower(e1).plus(Exponent.getPower(e2)));
                    factors.add(visit(newExponent, parent));
                }
                else if (e1 instanceof Polynomial && e2 instanceof Polynomial) {
                    Polynomial newPolynomial = multiply(((Polynomial) e1).getTerms(), ((Polynomial) e2).getTerms());
                    factors.add(visit(newPolynomial, parent));
                }
                else if (e1 instanceof Polynomial) {
                    Polynomial newPolynomial = multiply(((Polynomial) e1).getTerms(), Collections.singletonList(e2));
                    factors.add(visit(newPolynomial, parent));
                }
                else if (e2 instanceof Polynomial) {
                    Polynomial newPolynomial = multiply(Collections.singletonList(e1), ((Polynomial) e2).getTerms());
                    factors.add(visit(newPolynomial, parent));
                }
                else {
                    result.add(e2);
                    result.add(e1);
                }
            }
        }
        return (result.isEmpty() ? Constant.ONE : result.size() == 1 ? result.removeLast() : Term.of(result.stream().sorted(BasicComparison.factors())));
    }

    private static Expression multiply(Constant<?> e1, Constant<?> e2) {
        if (e1.isSameType(e2) && e1.isSameType(Integer.class)) {
            return Constant.of(e1.getValue().intValue() * e2.getValue().intValue());
        }
        else if (e1.isSameType(e2) && e1.isSameType(Long.class)) {
            return Constant.of(e1.getValue().longValue() * e2.getValue().longValue());
        }
        else if (e1.isSameType(e2) && e1.isSameType(Double.class)) {
            return Constant.of(e1.getValue().doubleValue() * e2.getValue().doubleValue());
        }
        else if (e1.isSameType(Double.class) || e2.isSameType(Double.class)) {
            return Constant.of(e1.getValue().doubleValue() * e2.getValue().doubleValue());
        }
        else if (e1.isSameType(Long.class) || e2.isSameType(Long.class)) {
            return Constant.of(e1.getValue().longValue() * e2.getValue().longValue());
        }
        else {
            throw new IllegalArgumentException(String.format("Don't know how to multiply %s and %s",
                    e1.getValue().getClass().getSimpleName(),
                    e2.getValue().getClass().getSimpleName()));
        }
    }

    private static Polynomial multiply(List<Expression> e1terms, List<Expression> e2terms) {
        List<Expression> terms = new ArrayList<>(e1terms.size() * e2terms.size());
        for (Expression e1term: e1terms) {
            for (Expression e2term: e2terms) {
                terms.add(e1term.times(e2term));
            }
        }
        return Polynomial.of(terms.stream().sorted(BasicComparison.terms()));
    }

    @Override
    public Expression visit(Polynomial expression, Expression parent) {
        PriorityQueue<Expression> terms = new PriorityQueue<>(BasicComparison.terms());
        terms.addAll(expression.getTerms().stream().map(e->visit(e, expression))
                .flatMap(e->e instanceof Polynomial ? ((Polynomial) e).getTerms().stream() : Stream.of(e))
                .collect(Collectors.toList()));
        Deque<Expression> result = new ArrayDeque<>();
        while (!terms.isEmpty()) {
            Expression e1 = terms.remove();
            if (e1 instanceof Polynomial) {
                terms.addAll(((Polynomial) e1).getTerms());
            }
            else if (isZero(e1)) {
            }
            else if (result.isEmpty()){
                result.addLast(e1);
            }
            else {
                Expression e2 = result.removeLast();
                if (e1 instanceof Constant<?> && e2 instanceof Constant<?>) {
                    terms.add(add((Constant<?>) e1, (Constant<?>) e2));
                }
                else if (!Term.getNonConstants(e1).isEmpty() &&
                        Term.getNonConstants(e1).equals(Term.getNonConstants(e2))) {
                    Expression constant1 = Term.getConstantCoefficient(e1);
                    Expression constant2 = Term.getConstantCoefficient(e2);
                    List<Expression> shared = Term.getNonConstants(e1);
                    Term e3 = Term.of(Stream.concat(shared.stream(), Stream.of(constant1.plus(constant2))).sorted(BasicComparison.factors()));
                    terms.add(visit(e3, parent));
                }
                else {
                    result.addLast(e2);
                    result.addLast(e1);
                }
            }
        }
        return result.isEmpty() ? Constant.ZERO : result.size() == 1 ? result.removeLast() : Polynomial.of(result.stream().sorted(BasicComparison.terms()));
    }

    private static Expression add(Constant<?> e1, Constant<?> e2) {
        if (e1.isSameType(e2) && e1.isSameType(Integer.class)) {
            return Constant.of(e1.getValue().intValue() + e2.getValue().intValue());
        }
        else if (e1.isSameType(e2) && e1.isSameType(Long.class)) {
            return Constant.of(e1.getValue().longValue() + e2.getValue().longValue());
        }
        else if (e1.isSameType(e2) && e1.isSameType(Double.class)) {
            return Constant.of(e1.getValue().doubleValue() + e2.getValue().doubleValue());
        }
        else if (e1.isSameType(Double.class) || e2.isSameType(Double.class)) {
            return Constant.of(e1.getValue().doubleValue() + e2.getValue().doubleValue());
        }
        else if (e1.isSameType(Long.class) || e2.isSameType(Long.class)) {
            return Constant.of(e1.getValue().longValue() + e2.getValue().longValue());
        }
        else {
            throw new IllegalArgumentException(String.format("Don't know how to add %s and %s",
                    e1.getValue().getClass().getSimpleName(),
                    e2.getValue().getClass().getSimpleName()));
        }
    }

    @Override
    public Expression visit(Exponent expression, Expression parent) {
        Expression power = visit(expression.getPower(), expression);
        Expression base = visit(expression.getBase(), expression);
        if (isOne(power)) {
            return base;
        }
        else if (isZero(base)) {
            return Constant.ZERO;
        }
        else if (base instanceof Constant<?> && power instanceof Constant<?> && ((Constant) base).isSameType(Double.class)) {
            return Constant.of(Math.pow(((Constant<?>)base).getValue().doubleValue(), ((Constant<?>) power).getValue().doubleValue()));
        }
        else if (base instanceof Constant && power instanceof Constant<?> && ((Constant) base).isSameType(Integer.class)) {
            return pow((Constant<Integer>) base, (Constant<Integer>) power);
        }
        else if (base instanceof Constant<?> && power instanceof Constant<?> && ((Constant) base).isSameType(Long.class)) {
            return lpow((Constant<Long>) base, (Constant<Long>) power);
        }
        else {
            return Exponent.of(base, power);
        }
    }

    @Override
    public Expression visit(Rational expression, Expression parent) {
        Expression denominator = visit(expression.getDenominator(), expression);
        Expression numerator = visit(expression.getNumerator(), expression);
        if (isOne(denominator)) {
            return numerator;
        }
        else if (isZero(numerator)) {
            return Constant.ZERO;
        }
        else if (denominator instanceof Rational && numerator instanceof Rational) {
            Expression newNumerator = ((Rational)numerator).getNumerator().times(((Rational)denominator).getDenominator());
            Expression newDenominator = ((Rational)numerator).getDenominator().times(((Rational)denominator).getNumerator());
            Rational newRational = Rational.of(newNumerator, newDenominator);
            return visit(newRational, parent);
        }
        else if (denominator instanceof Rational) {
            Expression newNumerator = numerator.times(((Rational) denominator).getDenominator());
            Expression newDenominator = ((Rational) denominator).getNumerator();
            Rational newRational = Rational.of(newNumerator, newDenominator);
            return visit(newRational, parent);
        }
        else if (denominator instanceof Trigonometric) {
            return visit(numerator.times(denominator.invert()), expression);
        }
        else {
            return Rational.of(numerator, denominator);
        }
    }

    @Override
    public Expression visit(Trigonometric expression, Expression parent) {
        return expression.withArgument(visit(expression.getArgument(), expression));
    }

    private static Expression pow(Constant<Integer> base, Constant<Integer> power) {
        if (power.getValue() == 0) {
            return Constant.ONE;
        }
        else if (power.getValue() == 1) {
            return base;
        }
        else if (power.getValue() > -1) {
            return Constant.of(pow(base.getValue(), power.getValue()));
        }
        else if (power.getValue() < 0) {
            return Constant.of(pow(base.getValue(), -1 * power.getValue())).invert();
        }
        else {
            return base.pow(power);
        }
    }

    private static Expression lpow(Constant<Long> base, Constant<Long> power) {
        if (power.getValue() == 0) {
            return Constant.ONE;
        }
        else if (power.getValue() == 1) {
            return base;
        }
        else if (power.getValue() > -1) {
            return Constant.of(lpow(base.getValue(), power.getValue()));
        }
        else if (power.getValue() < 0) {
            return Constant.of(lpow(base.getValue(), -1 * power.getValue())).invert();
        }
        else {
            return base.pow(power);
        }
    }

    private static int pow(int base, int power) {
        int res = 1;
        for (long idx = 0; idx < power; idx++) {
            res *= base;
        }
        return res;
    }

    private static long lpow(long base, long power) {
        long res = 1;
        for (long idx = 0; idx < power; idx++) {
            res *= base;
        }
        return res;
    }

    private static boolean isZero(Expression e) {
        return e.equals(Constant.ZERO) || e.equals(Constant.DOUBLE_ZERO);
    }

    private static boolean isOne(Expression e) {
        return e.equals(Constant.ONE) || e.equals(Constant.DOUBLE_ONE);
    }
}

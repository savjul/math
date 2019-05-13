package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;
import com.savjul.math.expression.simple.DoubleConstant;
import com.savjul.math.expression.simple.IntegerConstant;

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
                result.clear(); factors.clear(); result.add(IntegerConstant.ZERO);
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
                if (e1 instanceof IntegerConstant && e2 instanceof IntegerConstant) {
                    factors.add(multiply((IntegerConstant) e1, (IntegerConstant) e2));
                }
                else if (e1 instanceof DoubleConstant && e2 instanceof DoubleConstant) {
                    factors.add(multiply((DoubleConstant) e1, (DoubleConstant) e2));
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
        return (result.isEmpty() ? IntegerConstant.ONE : result.size() == 1 ? result.removeLast() : Term.of(result.stream().sorted(BasicComparison.factors())));
    }

    private static IntegerConstant multiply(IntegerConstant e1, IntegerConstant e2) {
        return IntegerConstant.of(e1.getValue() * e2.getValue());
    }

    private static DoubleConstant multiply(DoubleConstant e1, DoubleConstant e2) {
        return DoubleConstant.of(e1.getValue() * e2.getValue());
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
                if (e1 instanceof IntegerConstant && e2 instanceof IntegerConstant) {
                    terms.add(add((IntegerConstant) e1, (IntegerConstant) e2));
                }
                else if (e1 instanceof DoubleConstant && e2 instanceof DoubleConstant) {
                    terms.add(add((DoubleConstant) e1, (DoubleConstant) e2));
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
        return result.isEmpty() ? IntegerConstant.ZERO : result.size() == 1 ? result.removeLast() : Polynomial.of(result.stream().sorted(BasicComparison.terms()));
    }

    private static Expression add(IntegerConstant e1, IntegerConstant e2) {
        return IntegerConstant.of(e1.getValue() + e2.getValue());
    }

    private static Expression add(DoubleConstant e1, DoubleConstant e2) {
        return DoubleConstant.of(e1.getValue() + e2.getValue());
    }

    @Override
    public Expression visit(Exponent expression, Expression parent) {
        Expression power = visit(expression.getPower(), expression);
        Expression base = visit(expression.getBase(), expression);
        if (isOne(power)) {
            return base;
        }
        else if (isZero(base)) {
            return IntegerConstant.ZERO;
        }
        else if (base instanceof DoubleConstant && power instanceof DoubleConstant) {
            return DoubleConstant.of(Math.pow(((DoubleConstant)base).getValue(), ((DoubleConstant) power).getValue()));
        }
        else if (base instanceof IntegerConstant && power instanceof IntegerConstant) {
            return pow((IntegerConstant) base, (IntegerConstant) power);
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
            return IntegerConstant.ZERO;
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
        else {
            return Rational.of(numerator, denominator);
        }
    }

    private static Expression pow(IntegerConstant base, IntegerConstant power) {
        if (power.getValue() == 0) {
            return IntegerConstant.ONE;
        }
        else if (power.getValue() == 1) {
            return base;
        }
        else if (power.getValue() > -1) {
            return IntegerConstant.of(pow(base.getValue(), power.getValue()));
        }
        else if (power.getValue() < 0) {
            return IntegerConstant.of(pow(base.getValue(), -1 * power.getValue())).invert();
        }
        else {
            return base.pow(power);
        }
    }

    private static long pow(long base, long power) {
        long res = 1;
        for (long idx = 0; idx < power; idx++) {
            res *= base;
        }
        return res;
    }

    private static boolean isZero(Expression e) {
        return e.equals(IntegerConstant.ZERO) || e.equals(DoubleConstant.ZERO);
    }

    private static boolean isOne(Expression e) {
        return e.equals(IntegerConstant.ONE) || e.equals(DoubleConstant.ONE);
    }
}

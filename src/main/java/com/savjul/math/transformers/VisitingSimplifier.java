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
import java.util.stream.Stream;

public final class VisitingSimplifier extends TopDownVisitor<Deque<Expression>> {
    private static final VisitingSimplifier SIMPLIFIER = new VisitingSimplifier();
    private static final Function<Expression, Expression> INSTANCE = VisitingSimplifier::expand;

    public static Function<Expression, Expression> instance() { return INSTANCE; }

    private static Expression expand(Expression expression) {
        Deque<Expression> state = new ArrayDeque<>();
        SIMPLIFIER.visit(state, expression, null);
        return state.removeLast();
    }

    @Override
    public void visit(Deque<Expression> state, Expression expression, Expression parent) {
        if (expression.isCompound()) {
            super.visit(state, expression, parent);
        }
        else {
            state.addLast(expression);
        }
    }

    @Override
    public void visit(Deque<Expression> state, Term expression, Expression parent) {
        state.addLast(expression);
        super.visit(state, expression, parent);
        PriorityQueue<Expression> factors = new PriorityQueue<>(BasicComparison.termSimplify());
        Expression e;
        while ((e = state.removeLast()) != expression) {
            factors.add(e);
        }
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
                    visit(state, newRational, parent);
                    factors.add(state.removeLast());

                }
                else if (e1 instanceof Rational) {
                    Expression newNumerator = ((Rational) e1).getNumerator().times(e2);
                    Expression newDenominator = ((Rational) e1).getDenominator();
                    Rational newRational = Rational.of(newNumerator, newDenominator);
                    visit(state, newRational, parent);
                    factors.add(state.removeLast());
                }
                else if (e2 instanceof Rational) {
                    Expression newNumerator = ((Rational) e2).getNumerator().times(e1);
                    Expression newDenominator = ((Rational) e2).getDenominator();
                    Rational newRational = Rational.of(newNumerator, newDenominator);
                    visit(state, newRational, parent);
                    factors.add(state.removeLast());
                }
                else if (BasicComparison.getBase(e1).equals(BasicComparison.getBase(e2))) {
                    Exponent newExponent = Exponent.of(BasicComparison.getBase(e1), BasicComparison.getPower(e1).plus(BasicComparison.getPower(e2)));
                    visit(state, newExponent, parent);
                    factors.add(state.removeLast());
                }
                else if (e1 instanceof Polynomial && e2 instanceof Polynomial) {
                    Polynomial newPolynomial = multiply(((Polynomial) e1).getTerms(), ((Polynomial) e2).getTerms());
                    visit(state, newPolynomial, parent);
                    factors.add(state.removeLast());
                }
                else if (e1 instanceof Polynomial) {
                    Polynomial newPolynomial = multiply(((Polynomial) e1).getTerms(), Collections.singletonList(e2));
                    visit(state, newPolynomial, parent);
                    factors.add(state.removeLast());
                }
                else if (e2 instanceof Polynomial) {
                    Polynomial newPolynomial = multiply(Collections.singletonList(e1), ((Polynomial) e2).getTerms());
                    visit(state, newPolynomial, parent);
                    factors.add(state.removeLast());
                }
                else {
                    result.add(e2);
                    result.add(e1);
                }
            }
        }
        if (parent instanceof Term) {
            state.addAll(result);
        }
        else {
            state.addLast(result.isEmpty() ? IntegerConstant.ONE : result.size() == 1 ? result.removeLast() : Term.of(result.stream().sorted(BasicComparison.factors())));
        }
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
    public void visit(Deque<Expression> state, Polynomial expression, Expression parent) {
        state.addLast(expression);
        super.visit(state, expression, parent);
        PriorityQueue<Expression> terms = new PriorityQueue<>(BasicComparison.terms());
        Expression e;
        while ((e = state.removeLast()) != expression) {
            terms.add(e);
        }
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
                else if (!BasicComparison.getNonConstants(e1).isEmpty() &&
                        BasicComparison.getNonConstants(e1).equals(BasicComparison.getNonConstants(e2))) {
                    Expression constant1 = BasicComparison.getConstantCoefficient(e1);
                    Expression constant2 = BasicComparison.getConstantCoefficient(e2);
                    List<Expression> shared = BasicComparison.getNonConstants(e1);
                    Term e3 = Term.of(Stream.concat(shared.stream(), Stream.of(constant1.plus(constant2))).sorted(BasicComparison.factors()));
                    visit(state, e3, parent);
                    terms.add(state.removeLast());
                }
                else {
                    result.addLast(e2);
                    result.addLast(e1);
                }
            }
        }
        if (parent instanceof Polynomial) {
            state.addAll(result);
        }
        else {
            state.addLast(result.isEmpty() ? IntegerConstant.ZERO : result.size() == 1 ? result.removeLast() : Polynomial.of(result.stream().sorted(BasicComparison.terms())));
        }
    }

    private static Expression add(IntegerConstant e1, IntegerConstant e2) {
        return IntegerConstant.of(e1.getValue() + e2.getValue());
    }

    private static Expression add(DoubleConstant e1, DoubleConstant e2) {
        return DoubleConstant.of(e1.getValue() + e2.getValue());
    }

    @Override
    public void visit(Deque<Expression> state, Exponent expression, Expression parent) {
        super.visit(state, expression, parent);
        Expression power = state.removeLast();
        Expression base = state.removeLast();
        if (isOne(power)) {
            state.addLast(expression.getBase());
        }
        else if (isZero(base)) {
            state.addLast(IntegerConstant.ZERO);
        }
        else if (base instanceof DoubleConstant && power instanceof DoubleConstant) {
            state.addLast(DoubleConstant.of(Math.pow(((DoubleConstant)base).getValue(), ((DoubleConstant) power).getValue())));
        }
        else if (base instanceof IntegerConstant && power instanceof IntegerConstant) {
            state.addLast(pow((IntegerConstant) base, (IntegerConstant) power));
        }
        else {
            state.addLast(Exponent.of(base, power));
        }
    }

    @Override
    public void visit(Deque<Expression> state, Rational expression, Expression parent) {
        super.visit(state, expression, parent);
        Expression denominator = state.removeLast();
        Expression numerator = state.removeLast();
        if (isOne(denominator)) {
            state.addLast(numerator);
        }
        else if (isZero(numerator)) {
            state.addLast(IntegerConstant.ZERO);
        }
        else if (denominator instanceof Rational && numerator instanceof Rational) {
            Expression newNumerator = ((Rational)numerator).getNumerator().times(((Rational)denominator).getDenominator());
            Expression newDenominator = ((Rational)numerator).getDenominator().times(((Rational)denominator).getNumerator());
            Rational newRational = Rational.of(newNumerator, newDenominator);
            visit(state, newRational, parent);
        }
        else if (denominator instanceof Rational) {
            Expression newNumerator = numerator.times(((Rational) denominator).getDenominator());
            Expression newDenominator = ((Rational) denominator).getNumerator();
            Rational newRational = Rational.of(newNumerator, newDenominator);
            visit(state, newRational, parent);
        }
        else {
            state.addLast(Rational.of(numerator, denominator));
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

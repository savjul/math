package com.savjul.math.expression;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BasicSimplifier {
    private static final Simplifier INSTANCE = BasicSimplifier::simplify;

    public static Simplifier instance() {
        return INSTANCE;
    }

    private static Expression simplify(Expression expression) {
        if (expression instanceof Term) {
            return simplifyFactors(((Term) expression).getFactors().stream());
        }
        else if (expression instanceof Polynomial) {
            return simplifyTerms(((Polynomial) expression).getTerms().stream());
        }
        else if (expression instanceof Rational) {
            return simplify((Rational) expression);
        }
        else if (expression instanceof Exponent) {
            return simplify((Exponent) expression);
        }
        return expression;
    }

    private static Expression simplify(Rational expression) {
        if (isOne(expression.getDenominator())) {
            return simplify(expression.getNumerator());
        }
        else if (expression.getNumerator() instanceof Rational && expression.getDenominator() instanceof Rational) {
            Rational numerator = (Rational) expression.getNumerator();
            Rational denominator = (Rational) expression.getDenominator();
            return simplify(Rational.of(simplifyFactors(numerator.getNumerator(), denominator.getDenominator()),
                    simplifyFactors(numerator.getDenominator(), denominator.getNumerator())));
        }
        else if (expression.getDenominator() instanceof Rational) {
            return simplifyFactors(expression.getNumerator(), expression.getDenominator().invert());
        }
        return expression;
    }

    private static Expression simplify(Exponent expression) {
        Expression base = simplify(expression.getBase());
        Expression power = simplify(expression.getPower());
        if (power.equals(IntegerConstant.ONE) || power.equals(DoubleConstant.ONE) ) {
            return expression.getBase();
        }
        else if (base instanceof DoubleConstant && power instanceof DoubleConstant) {
            return DoubleConstant.of(Math.pow(((DoubleConstant)base).getValue(), ((DoubleConstant) power).getValue()));
        }
        else if (base instanceof IntegerConstant && power instanceof IntegerConstant) {
            return pow((IntegerConstant) base, (IntegerConstant) power);
        }
        return Exponent.of(base, power);
    }

    private static Expression simplifyTerms(Expression... expressions) {
        return simplifyTerms(Stream.of(expressions));
    }

    private static Expression simplifyTerms(Stream<Expression> expressionStream) {
        PriorityQueue<Expression> input = flatten(expressionStream, Polynomial::getTerms, Polynomial.class).stream().collect(Collectors.toCollection(new Supplier<PriorityQueue<Expression>>() {
            @Override
            public PriorityQueue<Expression> get() {
                return new PriorityQueue<>(BasicComparison.terms());
            }
        }));
        Deque<Expression> result = new ArrayDeque<>();
        while (!input.isEmpty()) {
            Expression e1 = simplify(input.remove());
            if (e1 instanceof Polynomial) {
                input.addAll(((Polynomial) e1).getTerms());
            }
            else if (isZero(e1)) {
            }
            else if (result.isEmpty()){
                result.addLast(e1);
            }
            else {
                Expression e2 = result.removeLast();
                if (e1 instanceof IntegerConstant && e2 instanceof IntegerConstant) {
                    input.add(add((IntegerConstant) e1, (IntegerConstant) e2));
                }
                else if (e1 instanceof DoubleConstant && e2 instanceof DoubleConstant) {
                    input.add(add((DoubleConstant) e1, (DoubleConstant) e2));
                }
                else if (!getNonConstants(e1).isEmpty() && getNonConstants(e1).equals(getNonConstants(e2))) {
                    Expression constant1 = getConstantCoefficient(e1);
                    Expression constant2 = getConstantCoefficient(e2);
                    List<Expression> shared = getNonConstants(e1);
                    Expression e3 = simplifyFactors(Stream.concat(shared.stream(), Stream.of(simplifyTerms(constant1, constant2))));
                    input.add(e3);
                }
                else {
                    result.addLast(e2);
                    result.addLast(e1);
                }
            }
        }
        return result.size() == 0 ? IntegerConstant.ZERO : result.size() == 1 ? result.removeLast() :
                Polynomial.of(result.stream().sorted(BasicComparison.terms()));
    }

    private static Expression simplifyFactors(Expression... expressions) {
        return simplifyFactors(Stream.of(expressions));
    }

    private static Expression simplifyFactors(Stream<Expression> expressionStream) {
        PriorityQueue<Expression> input = flatten(expressionStream, Term::getFactors, Term.class).stream().collect(Collectors.toCollection(new Supplier<PriorityQueue<Expression>>() {
            @Override
            public PriorityQueue<Expression> get() {
                return new PriorityQueue<>(BasicComparison.factors());
            }
        }));
        Deque<Expression> result = new ArrayDeque<>();
        while (!input.isEmpty()) {
            Expression e1 = simplify(input.remove());
            if (isZero(e1)) {
                return IntegerConstant.ZERO;
            }
            else if (isOne(e1)) {
            }
            else if (e1 instanceof Term) {
                input.addAll(((Term) e1).getFactors());
            }
            else if (result.isEmpty() ){
                result.addLast(e1);
            }
            else {
                Expression e2 = result.removeLast();
                if (e1 instanceof IntegerConstant && e2 instanceof IntegerConstant) {
                    result.add(multiply((IntegerConstant) e1, (IntegerConstant) e2));
                }
                else if (e1 instanceof DoubleConstant && e2 instanceof DoubleConstant) {
                    result.add(multiply((DoubleConstant) e1, (DoubleConstant) e2));
                }
                else if (e1 instanceof Rational && e2 instanceof Rational) {
                    result.add(multiply((Rational) e1, (Rational) e2));
                }
                else if (e1 instanceof Rational) {
                    result.add(simplify(Rational.of(simplifyFactors(((Rational) e1).getNumerator(), e2), ((Rational) e1).getDenominator())));
                }
                else if (getBase(e1).equals(getBase(e2))) {
                    result.add(pow(getBase(e1), simplifyTerms(getPower(e1), getPower(e2))));
                }
                else if (e1 instanceof Polynomial && e2 instanceof Polynomial) {
                    input.add(multiply((Polynomial) e1, (Polynomial) e2));
                }
                else if (e1 instanceof Polynomial) {
                    input.add(multiply(e2, (Polynomial) e1));
                }
                else if (e2 instanceof Polynomial) {
                    input.add(multiply(e1, (Polynomial) e2));
                }
                else {
                    result.add(e2);
                    result.add(e1);
                }
            }
        }
        return result.size() == 0 ? IntegerConstant.ONE : result.size() == 1 ? result.removeLast() :
                Term.of(result.stream().sorted(BasicComparison.factors()));
    }

    private static Expression add(IntegerConstant e1, IntegerConstant e2) {
        return IntegerConstant.of(e1.getValue() + e2.getValue());
    }

    private static Expression add(DoubleConstant e1, DoubleConstant e2) {
        return DoubleConstant.of(e1.getValue() + e2.getValue());
    }

    private static Expression multiply(IntegerConstant e1, IntegerConstant e2) {
        return IntegerConstant.of(e1.getValue() * e2.getValue());
    }

    private static Expression multiply(DoubleConstant e1, DoubleConstant e2) {
        return DoubleConstant.of(e1.getValue() * e2.getValue());
    }

    private static Expression multiply(Rational e1, Rational e2) {
        return simplify(Rational.of(simplifyFactors(e1.getNumerator(), e2.getNumerator()), simplifyFactors(e1.getDenominator(), e2.getDenominator())));
    }

    private static Expression multiply(Expression e, Polynomial p) {
        List<Expression> result = new ArrayList<>();
        for (Expression part: p.getTerms()) {
            result.add(simplifyFactors(e, part));
        }
        return simplifyTerms(result.stream());
    }

    private static Expression multiply(Polynomial p1, Polynomial p2) {
        List<Expression> result = new ArrayList<>();
        for (Expression part: p1.getTerms()) {
            result.add(simplifyFactors(Stream.of(part, p2)));
        }
        return simplifyTerms(result.stream());
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

    private static boolean isZero(Expression e) {
        return e.equals(IntegerConstant.ZERO) || e.equals(DoubleConstant.ZERO);
    }

    private static boolean isOne(Expression e) {
        return e.equals(IntegerConstant.ONE) || e.equals(DoubleConstant.ONE);
    }

    private static Expression getConstantCoefficient(Expression expression) {
        if (expression instanceof Term) {
            return simplifyFactors(((Term)expression).getFactors().stream().filter(Expression::isConstant));
        } else if (expression.isConstant()) {
            return expression;
        } else {
            return IntegerConstant.ONE;
        }
    }

    private static List<Expression> getNonConstants(Expression expression) {
        if (expression instanceof Term) {
            return ((Term)expression).getFactors().stream().filter(f->!f.isConstant()).collect(Collectors.toList());
        } else if (expression.isConstant()) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(expression);
        }
    }

    private static Expression getBase(Expression expression) {
        if (expression instanceof Exponent) {
            return ((Exponent) expression).getBase();
        }
        else {
            return expression;
        }
    }

    private static Expression getPower(Expression expression) {
        if (expression instanceof Exponent) {
            return ((Exponent) expression).getPower();
        }
        else {
            return IntegerConstant.ONE;
        }
    }

    private static Expression pow(Expression base, Expression power) {
        return base.pow(power);
    }

    private static long pow(long base, long power) {
        long res = 1;
        for (long idx = 0; idx < power; idx++) {
            res *= base;
        }
        return res;
    }

    private static <T> Collection<Expression> flatten(Stream<Expression> input, Function<T, List<Expression>> extract, Class<T> klass) {
        Deque<Expression> remaining = input.collect(Collectors.toCollection(ArrayDeque::new));
        List<Expression> result = new ArrayList<>();
        while (!remaining.isEmpty()) {
            Expression next = remaining.removeFirst();
            if (klass.isInstance(next)) {
                remaining.addAll(extract.apply(klass.cast(next)));
            }
            else {
                result.add(next);
            }
        }
        return result;
    }

    private BasicSimplifier() {}
}

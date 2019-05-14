package com.savjul.math.expression;

import com.savjul.math.expression.compound.Trigonometric;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Transcendental;
import com.savjul.math.expression.simple.Variable;
import com.savjul.math.transformers.Calculator;
import com.savjul.math.transformers.VariableExpander;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExpressionTest {
    @Test
    public void testPolynomialWithExponent() {
        Expression e = Variable.of("x").pow(Constant.of(2)).plus(Variable.of("x"));
        Assert.assertEquals("x^2 + x", e.toString());
    }

    @Test
    public void testPolynomialWithConstantMultiple() {
        Expression e1 = Variable.of("x").pow(Constant.of(3)).plus(Variable.of("x").pow(Constant.of(3))).simplify();
        Assert.assertEquals("2(x^3)", e1.toString());
    }

    @Test
    public void testTermMultiplicationByVariable() {
        Expression x = Variable.of("x");
        Expression x2 = Constant.of(2).times(Variable.of("x"));
        Expression res = x.times(x2).simplify();
        Assert.assertEquals("2(x^2)", res.toString());
    }

    @Test
    public void testBinomialCreation() {
        Expression e1 = Variable.of("x").plus(Constant.ONE);
        Expression e2 = Variable.of("y").plus(Constant.of(3));
        Expression res = e1.times(e2);
        Expression res2 = res.simplify();
        Assert.assertEquals("(x + 1)(y + 3)", res.toString());
        Assert.assertEquals("xy + 3x + y + 3", res2.toString());
    }

    @Test
    public void testPolynomialExponent() {
        Expression e1 = Variable.of("x").plus(Constant.ONE);
        Expression e2 = Variable.of("x");
        Expression res = e2.pow(e1);
        Assert.assertEquals("x^(x + 1)", res.toString());
    }

    @Test
    public void testSimplicationOfTwoVariableTerm() {
        Expression e1 = Variable.of("x").times(Variable.of("y")).times(Variable.of("x")).simplify();
        Assert.assertEquals("(x^2)y", e1.toString());
    }

    @Test
    public void testPolynomialMultiplication() {
        Expression e1 = Variable.of("x").plus(Constant.ONE);
        Expression e2 = Variable.of("x").plus(Constant.of(3));
        Expression res = e1.times(e2);
        Assert.assertEquals("(x + 1)(x + 3)", res.toString());
        Assert.assertEquals("x^2 + 4x + 3", res.simplify().toString());
    }

    @Test
    public void testNegativeNumbers() {
        Expression e1 = Variable.of("x").plus(Constant.ONE);
        Expression e2 = Variable.of("x").plus(Constant.MINUS_ONE);
        Expression res = e1.times(e2);
        Assert.assertEquals("(x + 1)(x + -1)", res.toString());
        Assert.assertEquals("x^2 + -1", res.simplify().toString());
    }

    @Test
    public void testExponentMultiplication() {
        Expression exp = Variable.of("x").plus(Constant.ONE);
        Expression e1 = Variable.of("x").pow(exp);
        Expression e2 = Variable.of("x").pow(exp);
        Expression res = e1.times(e2);
        Assert.assertEquals("x^(2x + 2)", res.simplify().toString());
    }

    @Test
    public void testPowerOfOne() {
        Expression e = Variable.of("x").pow(Constant.ONE).simplify();
        Assert.assertEquals(Variable.of("x"), e);
        Assert.assertEquals("x", e.toString());
    }

    @Test
    public void testBaseOfZero() {
        Expression e = Constant.ZERO.pow(Variable.of("x")).simplify();
        Assert.assertEquals(Constant.ZERO, e);
    }

    @Test
    public void testZeroCancel() {
        Expression e1 = Variable.of("x");
        Expression e2 = Variable.of("x").times(Constant.MINUS_ONE);
        Expression res = e1.plus(e2);
        Assert.assertEquals("0", res.simplify().toString());
    }

    @Test
    public void testCube() {
        Expression e1 = Variable.of("x");
        Expression res = e1.times(e1).times(e1).simplify();
        Assert.assertEquals("x^3", res.toString());
    }

    @Test
    public void testXtimesXpow2timesy() {
        Expression x = Variable.of("x");
        Expression y = Variable.of("y");
        Expression e1 = x.times(x).times(y);
        Expression res = x.times(e1).simplify();
        Assert.assertEquals("(x^3)y", res.toString());
    }

    @Test
    public void testytimesxtimeypow2() {
        Expression x = Variable.of("x");
        Expression y = Variable.of("y");
        Expression e1 = x.times(y).times(y);
        Expression res = y.times(e1).simplify();
        Assert.assertEquals("(y^3)x", res.toString());
    }

    @Test
    public void testxplus1timesxplus1() {
        Expression e1 = Variable.of("x").plus(Constant.of(1));
        Expression res = e1.times(e1).simplify();
        Assert.assertEquals("(x + 1)^2", res.toString());
    }

    @Test
    public void testXpowYtimeXpow1() {
        Expression e1 = Variable.of("x").pow(Variable.of("y"));
        Expression e2 = Variable.of("x");
        Expression res = e1.times(e2).simplify();
        Assert.assertEquals("x^(y + 1)", res.toString());
    }

    @Test
    public void testWithContext() {
        Expression e1 = Variable.of("x").pow(Variable.of("y"));
        Expression e2 = Variable.of("y").plus(Variable.of("x")).plus(Constant.of(3));
        Function<Expression, Expression> variableExpander = VariableExpander.get()
                .add("x", 3)
                .add("y", 5)
                .build();
        Expression res = e1.times(e2).simplify();
        Assert.assertEquals("(x^y)y + x^(y + 1) + 3(x^y)", res.toString());
        Assert.assertEquals("(3^5)5 + 3^(5 + 1) + 3(3^5)", variableExpander.apply(res).toString());
        Assert.assertEquals("2673", res.apply(variableExpander).simplify().toString());
    }

    @Test
    public void testIntegerDivision() {
        Expression e1 = Constant.of(4).divideBy(Constant.of(9));
        Assert.assertEquals("4/9", e1.toString());
    }

    @Test
    public void testIntegerMultiplicationByRational() {
        Expression e1 = Constant.of(4).divideBy(Constant.of(9));
        Expression e2 = Constant.of(2);
        Expression result = e2.times(e1).simplify();
        Assert.assertEquals("8/9", result.toString());
    }

    @Test
    public void testRationalMultiplicationByRational() {
        Expression e1 = Constant.of(4).divideBy(Constant.of(9));
        Expression e2 = Constant.of(3).divideBy(Constant.of(7));
        Expression result = e1.times(e2).simplify();
        Assert.assertEquals("12/63", result.toString());
    }

    @Test
    public void testRationalDivisionByRational() {
        Expression e1 = Constant.of(1).divideBy(Constant.of(2));
        Expression e2 = Constant.of(1).divideBy(Constant.of(7));
        Expression result = e1.divideBy(e2).simplify();
        Assert.assertEquals("7/2", result.toString());
    }

    @Test
    public void testDivisionByOne() {
        Expression e1 = Variable.of("x").divideBy(Constant.ONE).simplify();
        Assert.assertEquals(Variable.of("x"), e1);
    }

    @Test
    public void testVariableDivideBy() {
        Expression e1 = Constant.ONE.plus(Variable.of("x")).divideBy(Variable.of("x"));
        Assert.assertEquals("(1 + x)/x", e1.toString());
    }

    @Test
    public void testInversion() {
        Expression e1 = Variable.of("x").invert();
        Assert.assertEquals("1/x", e1.toString());
        Assert.assertEquals("x", e1.invert().simplify().toString());
    }

    @Test(expected = Exception.class)
    public void testDivisionByZero() {
        Constant.ONE.divideBy(Constant.ZERO);
    }

    @Test(expected = Exception.class)
    public void testEvaluationToDivisionByZero() {
        Expression e1 = Constant.ONE.divideBy(Variable.of("x"));
        Function<Expression, Expression> variableExpander = VariableExpander.get().add("x", 0).build();
        Expression e2 = e1.apply(variableExpander);
    }

    @Test
    public void testIntegerToNegativePower() {
        Expression e1 = Constant.of(5).pow(Constant.of(-2)).simplify();
        Assert.assertEquals(Constant.of(1).divideBy(Constant.of(25)), e1);
    }

    @Test
    public void testSine() {
        Expression sinx = Trigonometric.sin(Variable.of("x"));
        Assert.assertFalse(sinx.isConstant());
        Assert.assertEquals("sin(x)", sinx.toString());
        Assert.assertEquals("csc(x)", sinx.invert().toString());
    }

    @Test
    public void testTrigCalculation() {
        Trigonometric csc45 = Trigonometric.sin(Transcendental.PI.divideBy(Constant.of(4.))).invert();
        Assert.assertEquals("csc(π/4.0)", csc45.toString());
        Assert.assertEquals(2./Math.sqrt(2.), csc45.doubleValue(), 0.0000000001);
    }

    @Test
    public void testTrigSimplification() {
        Expression expression = Transcendental.PI.divideBy(Trigonometric.sin(Variable.of("x")));
        Assert.assertEquals("π/sin(x)", expression.toString());
        Expression simplified = expression.simplify();
        Assert.assertEquals("πcsc(x)", simplified.toString());
    }

    @Test(expected = Exception.class)
    public void testCalculationOnVariableFails() {
        Trigonometric sinx = Trigonometric.sin(Variable.of("x"));
        sinx.doubleValue();
    }
}

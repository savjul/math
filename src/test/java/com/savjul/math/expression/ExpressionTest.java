package com.savjul.math.expression;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {
    @Test
    public void testExpressionOrder() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.ONE);
        Assert.assertEquals(ExpressionConstants.POLYNOMIAL_ORDER, e1.order());
        Expression e2 = Variable.of("x").times(Variable.of("x")).times(IntegerConstant.ONE);
        Assert.assertEquals(ExpressionConstants.TERM_ORDER, e2.order());
        Expression e3 = Term.of(IntegerConstant.ONE);
        Assert.assertEquals(ExpressionConstants.INTEGER_ORDER_OTHER, e3.order());
        Expression e4 = Term.of(IntegerConstant.MINUS_ONE, Variable.of("x"));
        Assert.assertEquals(ExpressionConstants.INTEGER_ORDER_TERM, ((Term)e4).getFactors().get(0).order());
    }

    @Test
    public void testPolynomialWithExponent() {
        Expression e = Variable.of("x").pow(IntegerConstant.of(2)).plus(Variable.of("x"));
        Assert.assertEquals("x^2 + x", e.toString());
    }

    @Test
    public void testPolynomialWithConstantMultiple() {
        Expression e1 = Variable.of("x").pow(IntegerConstant.of(3)).plus(Variable.of("x").pow(IntegerConstant.of(3))).simplify();
        Assert.assertEquals("2x^3", e1.toString());
    }

    @Test
    public void testTermMultiplicationByVariable() {
        Expression x = Variable.of("x");
        Expression x2 = IntegerConstant.of(2).times(Variable.of("x"));
        Expression res = x.times(x2).simplify();
        Assert.assertEquals("2x^2", res.toString());
    }

    @Test
    public void testBinomialCreation() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.ONE);
        Expression e2 = Variable.of("y").plus(IntegerConstant.of(3));
        Expression res = e1.times(e2);
        Expression res2 = res.simplify();
        Assert.assertEquals("(x + 1)(y + 3)", res.toString());
        Assert.assertEquals("3x + xy + y + 3", res2.toString());
    }

    @Test
    public void testPolynomialExponent() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.ONE);
        Expression e2 = Variable.of("x");
        Expression res = e2.pow(e1);
        Assert.assertEquals("x^(x + 1)", res.toString());
    }

    @Test
    public void testSimplicationOfTwoVariableTerm() {
        Expression e1 = Variable.of("x").times(Variable.of("y")).times(Variable.of("x")).simplify();
        Assert.assertEquals("x^2y", e1.toString());
    }

    @Test
    public void testPolynomialMultiplication() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.ONE);
        Expression e2 = Variable.of("x").plus(IntegerConstant.of(3));
        Expression res = e1.times(e2);
        Assert.assertEquals("(x + 1)(x + 3)", res.toString());
        Assert.assertEquals("x^2 + 4x + 3", res.simplify().toString());
    }

    @Test
    public void testNegativeNumbers() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.ONE);
        Expression e2 = Variable.of("x").plus(IntegerConstant.MINUS_ONE);
        Expression res = e1.times(e2);
        Assert.assertEquals("(x + -1)(x + 1)", res.toString());
        Assert.assertEquals("x^2 + -1", res.simplify().toString());
    }

    @Test
    public void testExponentMultiplication() {
        Expression exp = Variable.of("x").plus(IntegerConstant.ONE);
        Expression e1 = Variable.of("x").pow(exp);
        Expression e2 = Variable.of("x").pow(exp);
        Expression res = e1.times(e2);
        Assert.assertEquals("x^(2x + 2)", res.simplify().toString());
    }

    @Test
    public void testPowerOfOne() {
        Expression e = Variable.of("x").pow(IntegerConstant.ONE);
        Assert.assertEquals(Variable.of("x"), e);
        Assert.assertEquals("x", e.toString());
    }

    @Test
    public void testBaseOfZero() {
        Expression e = IntegerConstant.ZERO.pow(Variable.of("x"));
        Assert.assertEquals(IntegerConstant.ZERO, e);
    }

    @Test
    public void testZeroCancel() {
        Expression e1 = Variable.of("x");
        Expression e2 = Variable.of("x").times(IntegerConstant.MINUS_ONE);
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
        Assert.assertEquals("x^3y", res.toString());
    }

    @Test
    public void testytimesxtimeypow2() {
        Expression x = Variable.of("x");
        Expression y = Variable.of("y");
        Expression e1 = x.times(y).times(y);
        Expression res = y.times(e1).simplify();
        Assert.assertEquals("xy^3", res.toString());
    }

    @Test
    public void testxplus1timesxplus1() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.of(1));
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
        Expression e2 = Variable.of("y").plus(Variable.of("x")).plus(IntegerConstant.of(3));
        Context c = ContextBuilder.get()
                .add("x", 3)
                .add("y", 5)
                .build();
        Expression res = e1.times(e2).simplify();
        Assert.assertEquals("3(x^y) + (x^y)y + x^(y + 1)", res.toString());
        Assert.assertEquals("3^(1 + 5) + 33^5 + 53^5", res.withContext(c).toString());
        Assert.assertEquals("2673", res.withContext(c).simplify().toString());
    }

    @Test
    public void testIntegerDivision() {
        Expression e1 = IntegerConstant.of(4).divideBy(IntegerConstant.of(9));
        Assert.assertEquals("4/9", e1.toString());
    }

    @Test
    public void testIntegerMultiplicationByRational() {
        Expression e1 = IntegerConstant.of(4).divideBy(IntegerConstant.of(9));
        Expression e2 = IntegerConstant.of(2);
        Expression result = e2.times(e1).simplify();
        Assert.assertEquals("8/9", result.toString());
    }

    @Test
    public void testRationalMultiplicationByRational() {
        Expression e1 = IntegerConstant.of(4).divideBy(IntegerConstant.of(9));
        Expression e2 = IntegerConstant.of(3).divideBy(IntegerConstant.of(7));
        Expression result = e1.times(e2).simplify();
        Assert.assertEquals("12/63", result.toString());
    }

    @Test
    public void testRationalDivisionByRational() {
        Expression e1 = IntegerConstant.of(1).divideBy(IntegerConstant.of(2));
        Expression e2 = IntegerConstant.of(1).divideBy(IntegerConstant.of(7));
        Expression result = e1.divideBy(e2).simplify();
        Assert.assertEquals("7/2", result.toString());
    }

    @Test
    public void testDivisionByOne() {
        Expression e1 = Variable.of("x").divideBy(IntegerConstant.ONE).simplify();
        Assert.assertEquals(Variable.of("x"), e1);
    }

    @Test
    public void testVariableDivideBy() {
        Expression e1 = IntegerConstant.ONE.plus(Variable.of("x")).divideBy(Variable.of("x"));
        Assert.assertEquals("(x + 1)/x", e1.toString());
    }

    @Test
    public void testInversion() {
        Expression e1 = Variable.of("x").invert();
        Assert.assertEquals("1/x", e1.toString());
        Assert.assertEquals("x", e1.invert().simplify().toString());
    }

    @Test(expected = Exception.class)
    public void testDivisionByZero() {
        IntegerConstant.ONE.divideBy(IntegerConstant.ZERO);
    }

    @Test(expected = Exception.class)
    public void testEvaluationToDivisionByZero() {
        Expression e1 = IntegerConstant.ONE.divideBy(Variable.of("x"));
        Context context = ContextBuilder.get().add("x", 0).build();
        e1.withContext(context);
    }

    @Test
    public void testIntegerToNegativePower() {
        Expression e1 = IntegerConstant.of(5).pow(IntegerConstant.of(-2)).simplify();
        Assert.assertEquals(IntegerConstant.of(1).divideBy(IntegerConstant.of(25)), e1);
    }
}

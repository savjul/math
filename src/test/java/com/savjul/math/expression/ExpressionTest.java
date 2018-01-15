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
        Assert.assertEquals(ExpressionConstants.INTEGER_ORDER_TERM, ((Term)e3).getFactors().get(0).order());
    }

    @Test
    public void testExpressionOrderComplex() {
        Expression e1 = Variable.of("a1").times(Variable.of("b1")).times(Variable.of("c1"));
        Expression e2 = Variable.of("a2").times(Variable.of("b1")).times(Variable.of("c2"));
        Expression e3 = e1.times(IntegerConstant.MINUS_ONE);
        Assert.assertEquals(-1, e1.compareTo(e2));
        Assert.assertEquals(-1, e3.compareTo(e2));
        Assert.assertEquals(-1, e3.compareTo(e2));
    }

    @Test
    public void testPolynomialWithExponent() {
        Expression e = Variable.of("x").pow(IntegerConstant.of(2)).plus(Variable.of("x"));
        Assert.assertEquals("x^2 + x", e.toString());
    }

    @Test
    public void testPolynomialWithConstantMultiple() {
        Expression e1 = Variable.of("x").pow(IntegerConstant.of(3)).plus(Variable.of("x").pow(IntegerConstant.of(3)));
        Assert.assertEquals("2x^3", e1.toString());
    }

    @Test
    public void testTermSimplification() {
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
        Assert.assertEquals("(x + 1)(y + 3)", res.toString());
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
        Expression res = Polynomial.multiply((Polynomial) e1, (Polynomial) e2);
           Assert.assertEquals("x^2 + 3x + x + 3", res.toString());
        Assert.assertEquals("x^2 + 4x + 3", res.simplify().toString());
    }

    @Test
    public void testNegativeNumbers() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.ONE);
        Expression e2 = Variable.of("x").plus(IntegerConstant.MINUS_ONE);
        Expression res = Polynomial.multiply((Polynomial) e1, (Polynomial) e2);
        Assert.assertEquals("x^2 + -1x + x + -1", res.toString());
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
    public void testZeroCancel() {
        Expression e1 = Variable.of("x");
        Expression e2 = Variable.of("x").times(IntegerConstant.MINUS_ONE);
        Expression res = e1.plus(e2);
        Assert.assertEquals("0", res.simplify().toString());
    }

    @Test
    public void testCube() {
        Expression e1 = Variable.of("x");
        Expression res = e1.times(e1).times(e1);
        Assert.assertEquals("x^3", res.toString());
    }

    @Test
    public void testXtimesXpow2timesy() {
        Variable x = Variable.of("x");
        Variable y = Variable.of("y");
        Expression e1 = x.times(x).times(y);
        Expression res = x.times(e1);
        Assert.assertEquals("x^3y", res.toString());
    }

    @Test
    public void testytimesxtimeypow2() {
        Variable x = Variable.of("x");
        Variable y = Variable.of("y");
        Expression e1 = x.times(y).times(y);
        Expression res = y.times(e1);
        Assert.assertEquals("xy^3", res.toString());
    }

    @Test
    public void testxplus1timesxplus1() {
        Expression e1 = Variable.of("x").plus(IntegerConstant.of(1));
        Expression res = e1.times(e1);
        Assert.assertEquals("(x + 1)^2", res.toString());
    }

    @Test
    public void testXpowYtimeXpow1() {
        Expression e1 = Variable.of("x").pow(Variable.of("y"));
        Expression e2 = Variable.of("x");
        Expression res = e1.times(e2);
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
        Expression res = e1.times(e2);
        Assert.assertEquals("(x^y)(x + y + 3)", res.toString());
        Assert.assertEquals("3^5(3 + 3 + 5)", res.withContext(c).toString());
        Assert.assertEquals("2673", res.withContext(c).simplify().toString());
    }
}

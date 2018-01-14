package com.savjul.math.expression;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionTest {
    @Test
    public void testExpressionOrder() {
        Expression e1 = Variable.of("x").add(IntegerConstant.ONE);
        Assert.assertEquals(ExpressionConstants.POLYNOMIAL_ORDER, e1.order());
        Expression e2 = Variable.of("x").multiply(Variable.of("x")).multiply(IntegerConstant.ONE);
        Assert.assertEquals(ExpressionConstants.TERM_ORDER, e2.order());
        Expression e3 = Term.of(IntegerConstant.ONE);
        Assert.assertEquals(ExpressionConstants.INTEGER_ORDER_OTHER, e3.order());
        Assert.assertEquals(ExpressionConstants.INTEGER_ORDER_TERM, ((Term)e3).getFactors().get(0).order());
    }

    @Test
    public void testExpressionOrderComplex() {
        Expression e1 = Variable.of("a1").multiply(Variable.of("b1")).multiply(Variable.of("c1"));
        Expression e2 = Variable.of("a2").multiply(Variable.of("b1")).multiply(Variable.of("c2"));
        Expression e3 = e1.multiply(IntegerConstant.MINUS_ONE);
        Assert.assertEquals(-1, e1.compareTo(e2));
        Assert.assertEquals(-1, e3.compareTo(e2));
        Assert.assertEquals(-1, e3.compareTo(e2));
    }

    @Test
    public void testPolynomialWithExponent() {
        Expression e = Variable.of("x").exp(IntegerConstant.of(2)).add(Variable.of("x"));
        Assert.assertEquals("x^2 + x", e.toString());
    }

    @Test
    public void testPolynomialWithConstantMultiple() {
        Expression e1 = Variable.of("x").exp(IntegerConstant.of(3)).add(Variable.of("x").exp(IntegerConstant.of(3)));
        Assert.assertEquals("2x^3", e1.toString());
    }

    @Test
    public void testTermSimplification() {
        Expression x = Variable.of("x");
        Expression x2 = IntegerConstant.of(2).multiply(Variable.of("x"));
        Expression res = x.multiply(x2).simplify();
        Assert.assertEquals("2x^2", res.toString());
    }

    @Test
    public void testBinomialCreation() {
        Expression e1 = Variable.of("x").add(IntegerConstant.ONE);
        Expression e2 = Variable.of("y").add(IntegerConstant.of(3));
        Expression res = e1.multiply(e2);
        Assert.assertEquals("(x + 1)(y + 3)", res.simplify().toString());
    }

    @Test
    public void testPolynomialExponent() {
        Expression e1 = Variable.of("x").add(IntegerConstant.ONE);
        Expression e2 = Variable.of("x");
        Expression res = e2.exp(e1);
        Assert.assertEquals("x^(x + 1)", res.toString());
    }

    @Test
    public void testSimplicationOfTwoVariableTerm() {
        Expression e1 = Variable.of("x").multiply(Variable.of("y")).multiply(Variable.of("x")).simplify();
        Assert.assertEquals("x^2y", e1.toString());
    }

    @Test
    public void testPolynomialMultiplication() {
        Expression e1 = Variable.of("x").add(IntegerConstant.ONE);
        Expression e2 = Variable.of("x").add(IntegerConstant.of(3));
        Expression res = Polynomial.multiply((Polynomial) e1, (Polynomial) e2);
        Assert.assertEquals("x^2 + 3x + x + 3", res.toString());
        Assert.assertEquals("x^2 + 4x + 3", res.simplify().toString());
    }

    @Test
    public void testNegativeNumbers() {
        Expression e1 = Variable.of("x").add(IntegerConstant.ONE);
        Expression e2 = Variable.of("x").add(IntegerConstant.MINUS_ONE);
        Expression res = Polynomial.multiply((Polynomial) e1, (Polynomial) e2);
        Assert.assertEquals("x^2 + -1x + x + -1", res.toString());
        Assert.assertEquals("x^2 + -1", res.simplify().toString());
    }

    @Test
    public void testExponentMultiplication() {
        Expression exp = Variable.of("x").add(IntegerConstant.ONE);
        Expression e1 = Variable.of("x").exp(exp);
        Expression e2 = Variable.of("x").exp(exp);
        Expression res = e1.multiply(e2);
        Assert.assertEquals("x^(2x + 2)", res.simplify().toString());
    }

    @Test
    public void testZeroCancel() {
        Expression e1 = Variable.of("x");
        Expression e2 = Variable.of("x").multiply(IntegerConstant.MINUS_ONE);
        Expression res = e1.add(e2);
        Assert.assertEquals("0", res.simplify().toString());
    }
}

package com.savjul.math.expression;

import org.junit.Assert;
import org.junit.Test;

public final class ExpressionComparisonTest {
    @Test
    public void testVariableComparedToConstant() {
        Expression e1 = Variable.of("x");
        Expression e2 = IntegerConstant.of(1);
        Assert.assertEquals(1, e2.compareTo(e1));
    }

    @Test
    public void testVariableTermComparedToConstant() {
        Expression e1 = IntegerConstant.of(4).times(Variable.of("x"));
        Expression e2 = IntegerConstant.of(1);
        Assert.assertEquals(1, e2.compareTo(e1));
    }

    @Test
    public void testExpressionOrderComplex() {
        Expression e1 = Variable.of("a1").times(Variable.of("b1")).times(Variable.of("c1")).simplify();
        Expression e2 = Variable.of("a2").times(Variable.of("b1")).times(Variable.of("c2")).simplify();
        Expression e3 = e1.times(IntegerConstant.MINUS_ONE);
        Assert.assertEquals(-1, e1.compareTo(e2));
        Assert.assertEquals(-1, e3.compareTo(e2));
        Assert.assertEquals(-1, e3.compareTo(e2));
    }
}

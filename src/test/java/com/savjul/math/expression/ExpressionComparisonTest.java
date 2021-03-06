package com.savjul.math.expression;

import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Variable;
import com.savjul.math.transformers.BasicComparison;
import org.junit.Assert;
import org.junit.Test;

public final class ExpressionComparisonTest {
    @Test
    public void testVariableComparedToConstant() {
        Expression e1 = Variable.of("x");
        Expression e2 = Constant.of(1);
        Assert.assertEquals(1, BasicComparison.factors().compare(e1, e2));
    }

    @Test
    public void testVariableTermComparedToConstant() {
        Expression e1 = Constant.of(4).times(Variable.of("x"));
        Expression e2 = Constant.of(1);
        Assert.assertEquals(1, BasicComparison.factors().compare(e1, e2));
    }

    @Test
    public void testExpressionOrderComplex() {
        Expression e1 = Variable.of("a1").times(Variable.of("b1")).times(Variable.of("c1")).simplify();
        Expression e2 = Variable.of("a2").times(Variable.of("b1")).times(Variable.of("c2")).simplify();
        Expression e3 = e1.times(Constant.MINUS_ONE);
        Assert.assertEquals(-1, BasicComparison.factors().compare(e1, e2));
        Assert.assertEquals(-1, BasicComparison.factors().compare(e2, e3));
    }
}

package com.savjul.math.expression;

import com.savjul.math.expression.simple.Constant;
import org.junit.Assert;
import org.junit.Test;

public final class ConstantTest {
    @Test
    public void testDouble() {
        Assert.assertEquals(Constant.of(12.), Constant.of(10.).plus(Constant.of(2.)).simplify());
        Assert.assertEquals(Constant.of(20.), Constant.of(10.).times(Constant.of(2.)).simplify());
        Assert.assertEquals(Constant.of(100.0), Constant.of(10.0).pow(Constant.of(2.)).simplify());
        Assert.assertEquals(Constant.DOUBLE_ONE, Constant.of(123.45).pow(Constant.of(0.)).simplify());
    }
}

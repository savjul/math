package com.savjul.math.expression;

import com.savjul.math.expression.simple.DoubleConstant;
import org.junit.Assert;
import org.junit.Test;

public final class DoubleConstantTest {
    @Test
    public void testDouble() {
        Assert.assertEquals(DoubleConstant.of(12.), DoubleConstant.of(10.).plus(DoubleConstant.of(2.)).simplify());
        Assert.assertEquals(DoubleConstant.of(20.), DoubleConstant.of(10.).times(DoubleConstant.of(2.)).simplify());
        Assert.assertEquals(DoubleConstant.of(100.0), DoubleConstant.of(10.0).pow(DoubleConstant.of(2.)).simplify());
        Assert.assertEquals(DoubleConstant.ONE, DoubleConstant.of(123.45).pow(DoubleConstant.of(0.)).simplify());
    }
}

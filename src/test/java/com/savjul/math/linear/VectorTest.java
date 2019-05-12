package com.savjul.math.linear;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.IntegerConstant;
import com.savjul.math.expression.simple.Variable;
import com.savjul.math.transformers.VariableExpander;
import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public final class VectorTest {
    @Test
    public void testVectorAddition() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Vector v2 = Vector.of(Variable.of("x")
                .plus(IntegerConstant.ONE), Variable.of("y"), IntegerConstant.MINUS_ONE.times(Variable.of("z")));
        Vector res = v1.plus(v2).simplify();
        Assert.assertEquals("[2x + 1, 2y, 0]", res.toString());
    }

    @Test
    public void testMultiplicationByScalar() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Vector res = v1.times(Variable.of("x")).times(IntegerConstant.of(4)).simplify();
        Assert.assertEquals("[4(x^2), 4xy, 4xz]", res.toString());
    }

    @Test
    public void testDotProduct() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Expression res = v1.dot(v1).simplify();
        Assert.assertEquals("x^2 + y^2 + z^2", res.toString());
    }

    @Test
    public void testDotProductWithIntegerConstants() {
        Vector v1 = Vector.of(IntegerConstant.of(3), IntegerConstant.of(2), IntegerConstant.of(1));
        Expression res = v1.dot(v1).simplify();
        Assert.assertEquals("14", res.toString());
    }

    @Test
    public void testCrossProductAgainstSelf() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Vector res = v1.cross(v1);
        Assert.assertEquals("[0, 0, 0]", res.simplify().toString());
        Assert.assertEquals(Vector.ZERO3, res.simplify());
    }

    @Test
    public void testCrossProduct() {
        Vector v1 = Vector.of(Variable.of("x"), IntegerConstant.ZERO, IntegerConstant.ZERO);
        Vector v2 = Vector.of(IntegerConstant.ZERO, Variable.of("y"), IntegerConstant.ZERO);
        Vector res = v1.cross(v2).simplify();
        Assert.assertEquals("[0, 0, xy]", res.simplify().toString());
    }

    @Test
    public void testCrossProductBasis() {
        Vector ixj = Vector.i.cross(Vector.j);
        Vector jxk = Vector.j.cross(Vector.k);
        Vector kxi = Vector.k.cross(Vector.i);

        Assert.assertEquals(Vector.k, ixj.simplify());
        Assert.assertEquals(Vector.i, jxk.simplify());
        Assert.assertEquals(Vector.j, kxi.simplify());
    }

    @Test
    public void testAntiCommutativity() {
        Vector jxi = Vector.j.cross(Vector.i);
        Vector kxj = Vector.k.cross(Vector.j);
        Vector ixk = Vector.i.cross(Vector.k);

        Assert.assertEquals(Vector.k.times(IntegerConstant.MINUS_ONE).simplify(), jxi.simplify());
        Assert.assertEquals(Vector.i.times(IntegerConstant.MINUS_ONE).simplify(), kxj.simplify());
        Assert.assertEquals(Vector.j.times(IntegerConstant.MINUS_ONE).simplify(), ixk.simplify());
    }

    @Test
    public void testCyclicalIdentity() {
        // (A x B ) x C = (A dot C)B - (B dot C)A
        Vector a = Vector.of(Variable.of("a1"), Variable.of("a2"), Variable.of("a3"));
        Vector b = Vector.of(Variable.of("b1"), Variable.of("b2"), Variable.of("b3"));
        Vector c = Vector.of(Variable.of("c1"), Variable.of("c2"), Variable.of("c3"));
        Vector lhs = a.cross(b).cross(c);
        lhs = lhs.simplify();
        Vector rhs = b.times(a.dot(c)).plus(a.times(b.dot(c)).times(IntegerConstant.MINUS_ONE));
        rhs = rhs.simplify();
        Assert.assertEquals(lhs.getValues()[0], rhs.getValues()[0]);
        Assert.assertEquals(lhs.getValues()[1], rhs.getValues()[1]);
        Assert.assertEquals(lhs.getValues()[2], rhs.getValues()[2]);
        Assert.assertEquals(lhs, rhs);
    }

    @Test
    public void testDotProductWithContext() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Expression res = v1.dot(v1).simplify();
        Assert.assertEquals("x^2 + y^2 + z^2", res.toString());
        Function<Expression, Expression> variableExpander = VariableExpander.get()
                .add("x", 1)
                .add("y", 2)
                .add("z", 3).build();

        Expression res2 = res.apply(variableExpander);
        Assert.assertEquals("1^2 + 2^2 + 3^2", res2.toString());
        Assert.assertEquals("14", res2.simplify().toString());

        Vector v2 = v1.apply(variableExpander);
        Assert.assertEquals("14", v2.dot(v2).simplify().toString());
    }
}

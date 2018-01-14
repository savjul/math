package com.savjul.math.linear;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.IntegerConstant;
import com.savjul.math.expression.Term;
import com.savjul.math.expression.Variable;
import org.junit.Assert;
import org.junit.Test;

public class VectorTest {
    @Test
    public void testVectorAddition() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Vector v2 = Vector.of(Variable.of("x")
                .add(IntegerConstant.of(1)), Variable.of("y"), Term.of(IntegerConstant.of(-1), Variable.of("z")));
        Vector res = v1.add(v2).simplify();
        Assert.assertEquals("[2x + 1, 2y, 0]", res.toString());
    }

    @Test
    public void testMultiplicationByScalar() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Vector res = v1.multiply(Variable.of("x")).multiply(IntegerConstant.of(4));
        Assert.assertEquals("[4x^2, 4xy, 4xz]", res.toString());
    }

    @Test
    public void testDotProduct() {
        Vector v1 = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));
        Expression res = v1.dot(v1);
        Assert.assertEquals("x^2 + y^2 + z^2", res.toString());
    }

    @Test
    public void testDotProductWithIntegerConstants() {
        Vector v1 = Vector.of(IntegerConstant.of(3), IntegerConstant.of(2), IntegerConstant.of(1));
        Expression res = v1.dot(v1);
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
        Vector v1 = Vector.of(Variable.of("x"), IntegerConstant.of(0), IntegerConstant.of(0));
        Vector v2 = Vector.of(IntegerConstant.of(0), Variable.of("y"), IntegerConstant.of(0));
        Vector res = v1.cross(v2);
        Assert.assertEquals("[0, 0, xy]", res.simplify().toString());
    }

    @Test
    public void testCrossProductBasis() {
        Vector ixj = Vector.i.cross(Vector.j);
        Vector jxk = Vector.j.cross(Vector.k);
        Vector kxi = Vector.k.cross(Vector.i);

        Assert.assertEquals(Vector.k, ixj);
        Assert.assertEquals(Vector.i, jxk);
        Assert.assertEquals(Vector.j, kxi);
    }

    @Test
    public void testAntiCommutativity() {
        IntegerConstant minus1 = IntegerConstant.of(-1);

        Vector jxi = Vector.j.cross(Vector.i);
        Vector kxj = Vector.k.cross(Vector.j);
        Vector ixk = Vector.i.cross(Vector.k);

        Assert.assertEquals(Vector.k.multiply(minus1), jxi);
        Assert.assertEquals(Vector.i.multiply(minus1), kxj);
        Assert.assertEquals(Vector.j.multiply(minus1), ixk);
    }

}

package com.savjul.math.linear;

import com.savjul.math.expression.*;
import org.junit.Assert;
import org.junit.Test;

public class Matrix2DTest {
    @Test
    public void testMatrix() {
        Matrix2D matrix2D = Matrix2D.of(new Expression[][] {
                {IntegerConstant.ONE, IntegerConstant.ZERO, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ONE, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ONE },
        });
        Assert.assertEquals("[[1, 0, 0], [0, 1, 0], [0, 0, 1]]", matrix2D.toString());
        Assert.assertEquals(IntegerConstant.ONE, matrix2D.get(0, 0));
        Assert.assertEquals(matrix2D, Matrix2D.getI(3));
    }

    @Test
    public void testMatrixAddition() {
        Matrix2D I3 = Matrix2D.getI(3);

        Matrix2D matrix2D2 = Matrix2D.of(new Expression[][] {
                {IntegerConstant.ONE, IntegerConstant.ZERO, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ONE, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ONE },
        });

        Matrix2D matrix2Dres = Matrix2D.of(new Expression[][] {
                {IntegerConstant.ONE.plus(IntegerConstant.ONE), IntegerConstant.ZERO, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ONE.plus(IntegerConstant.ONE), IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.ONE.plus(IntegerConstant.ONE) },
        });

        Matrix2D res = I3.plus(matrix2D2);
        Assert.assertEquals(matrix2Dres, res);
    }

    @Test
    public void testMatrixScalarMultiplication() {
        Matrix2D I3 = Matrix2D.getI(3);

        Matrix2D expectedResult = Matrix2D.of(new Expression[][] {
                {IntegerConstant.MINUS_ONE, IntegerConstant.ZERO, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.MINUS_ONE, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.MINUS_ONE },
        });
        Matrix2D result = I3.times(IntegerConstant.MINUS_ONE);
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void testMatrixMatrixMultiplication() {
        // textbook example
        Matrix2D A = Matrix2D.of(new Expression[][] {
                {IntegerConstant.of(1), IntegerConstant.of(2), IntegerConstant.of(4) },
                {IntegerConstant.of(2), IntegerConstant.of(6), IntegerConstant.of(0) },
        });
        Matrix2D B = Matrix2D.of(new Expression[][] {
                {IntegerConstant.of(4), IntegerConstant.of(1), IntegerConstant.of(4), IntegerConstant.of(3) },
                {IntegerConstant.of(0), IntegerConstant.of(-1), IntegerConstant.of(3), IntegerConstant.of(1) },
                {IntegerConstant.of(2), IntegerConstant.of(7), IntegerConstant.of(5), IntegerConstant.of(2) },
        });
        Matrix2D AB = Matrix2D.of(new Expression[][] {
                {IntegerConstant.of(12), IntegerConstant.of(27), IntegerConstant.of(30), IntegerConstant.of(13) },
                {IntegerConstant.of(8), IntegerConstant.of(-4), IntegerConstant.of(26), IntegerConstant.of(12) },
        });
        Matrix2D result = A.times(B);
        Assert.assertEquals(AB, result);
    }

    @Test
    public void testMatrixTranspose() {
        Matrix2D A = Matrix2D.of(new Expression[][] {
                {IntegerConstant.of(1), IntegerConstant.of(2), IntegerConstant.of(4) },
                {IntegerConstant.of(2), IntegerConstant.of(6), IntegerConstant.of(0) },
        });

        Matrix2D AT = Matrix2D.of(new Expression[][] {
                {IntegerConstant.of(1), IntegerConstant.of(2) },
                {IntegerConstant.of(2), IntegerConstant.of(6) },
                {IntegerConstant.of(4), IntegerConstant.of(0) },
        });
        Assert.assertEquals(AT, A.transpose());
    }

    @Test
    public void testMatrixVectorMultiplication() {
        Matrix2D I3 = Matrix2D.getI(3);

        Vector vector = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));

        Assert.assertEquals(vector, I3.times(vector));
    }

    @Test
    public void testTrace() {
        Matrix2D A = Matrix2D.of(new Expression[][] {
                { Variable.of("a1.1"), Variable.of("a1.2"), Variable.of("a1.3"), Variable.of("a1.4"), },
                { Variable.of("a2.1"), Variable.of("a2.2"), Variable.of("a2.3"), Variable.of("a2.4"), },
                { Variable.of("a3.1"), Variable.of("a3.2"), Variable.of("a3.3"), Variable.of("a3.4"), },
                { Variable.of("a4.1"), Variable.of("a4.2"), Variable.of("a4.3"), Variable.of("a4.4"), },
        });
        Matrix2D B = Matrix2D.of(new Expression[][] {
                { Variable.of("b1.1"), Variable.of("b1.2"), Variable.of("b1.3"), Variable.of("b1.4"), },
                { Variable.of("b2.1"), Variable.of("b2.2"), Variable.of("b2.3"), Variable.of("b2.4"), },
                { Variable.of("b3.1"), Variable.of("b3.2"), Variable.of("b3.3"), Variable.of("b3.4"), },
                { Variable.of("b4.1"), Variable.of("b4.2"), Variable.of("b4.3"), Variable.of("b4.4"), },
        });
        Assert.assertEquals(A.plus(B).trace(), A.trace().plus(B.trace()));
    }

    @Test
    public void testWithContext() {
        Matrix2D A = Matrix2D.of(new Expression[][] {
                { Variable.of("a1"), Variable.of("a2"), Variable.of("a3"), },
                { Variable.of("b1"), Variable.of("b2"), Variable.of("b3"), },
                { Variable.of("c1"), Variable.of("c2"), Variable.of("c3"), },
        });
        Matrix2D B = Matrix2D.of(new Expression[][] {
                { Variable.of("x1"), Variable.of("x2"), Variable.of("x3"), },
                { Variable.of("y1"), Variable.of("y2"), Variable.of("y3"), },
                { Variable.of("z1"), Variable.of("z2"), Variable.of("z3"), },
        });
        Context context = ContextBuilder.get()
                .add("a1", Variable.of("x1")).add("a2", Variable.of("x2")).add("a3", Variable.of("x3"))
                .add("b1", Variable.of("y1")).add("b2", Variable.of("y2")).add("b3", Variable.of("y3"))
                .add("c1", Variable.of("z1")).add("c2", Variable.of("z2")).add("c3", Variable.of("z3"))
                .build();
        Assert.assertEquals(B, A.withContext(context));
    }
}

package com.savjul.math.linear;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.IntegerConstant;
import com.savjul.math.expression.Variable;
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
    public void testMatrixVectorMultiplication() {
        Matrix2D I3 = Matrix2D.getI(3);

        Vector vector = Vector.of(Variable.of("x"), Variable.of("y"), Variable.of("z"));

        Assert.assertEquals(vector, I3.times(vector));
    }
}

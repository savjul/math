package com.savjul.math.linear;

import com.savjul.math.expression.*;
import org.junit.Assert;
import org.junit.Test;

public class Matrix2DTest {
    public Expression[][] variables(String[][] names) {
        Expression[][] variables = new Expression[names.length][];
        for (int idx = 0; idx < names.length; idx++) {
            Expression[] row = new Expression[names[idx].length];
            for (int jdx = 0; jdx < row.length; jdx++) {
                row[jdx] = Variable.of(names[idx][jdx]);
            }
            variables[idx] = row;
        }
        return variables;
    }

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
        Assert.assertEquals(matrix2Dres.simplify(), res.simplify());
    }

    @Test
    public void testMatrixScalarMultiplication() {
        Matrix2D I3 = Matrix2D.getI(3);

        Matrix2D expectedResult = Matrix2D.of(new Expression[][] {
                {IntegerConstant.MINUS_ONE, IntegerConstant.ZERO, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.MINUS_ONE, IntegerConstant.ZERO },
                {IntegerConstant.ZERO, IntegerConstant.ZERO, IntegerConstant.MINUS_ONE },
        });
        Matrix2D result = I3.times(IntegerConstant.MINUS_ONE).simplify();
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
        Matrix2D result = A.times(B).simplify();
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

        Vector timesI3 = I3.times(vector);
        timesI3 = timesI3.simplify();
        Assert.assertEquals(vector, timesI3);
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
        Assert.assertEquals(A.plus(B).trace().simplify(), A.trace().plus(B.trace()).simplify());
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

    @Test
    public void testSimpleDeterminant() {
        Matrix2D A = Matrix2D.of(new Expression[][] {
                { IntegerConstant.of(3), IntegerConstant.of(-4), },
                { IntegerConstant.of(2), IntegerConstant.of(6), },
        });
        Assert.assertEquals(IntegerConstant.of(26), A.det().simplify());
    }

    @Test
    public void testDeterminant() {
        Matrix2D A = Matrix2D.of(new Expression[][] {
                { IntegerConstant.of(3), IntegerConstant.of(1), IntegerConstant.of(0)},
                { IntegerConstant.of(-2), IntegerConstant.of(-4), IntegerConstant.of(3) },
                { IntegerConstant.of(5), IntegerConstant.of(4), IntegerConstant.of(-2) },
        });
        Assert.assertEquals(IntegerConstant.of(-1), A.det().simplify());
    }

    @Test
    public void testDeterminateWithVariables() {
        Matrix2D A = Matrix2D.of(variables(new String[][] {
                { "a11", "a12", "a13", },
                { "a21", "a22", "a23", },
                { "a31", "a32", "a33", },
        }));
        Expression result = IntegerConstant.ZERO.plus(
                Variable.of("a11").times(Variable.of("a22")).times(Variable.of("a33"))
        ).plus(
                Variable.of("a12").times(Variable.of("a23")).times(Variable.of("a31"))
        ).plus(
                Variable.of("a13").times(Variable.of("a21")).times(Variable.of("a32"))
        ).plus(
                Variable.of("a13").times(Variable.of("a22")).times(Variable.of("a31")).times(IntegerConstant.MINUS_ONE)
        ).plus(
                Variable.of("a12").times(Variable.of("a21")).times(Variable.of("a33")).times(IntegerConstant.MINUS_ONE)
        ).plus(
                Variable.of("a11").times(Variable.of("a23")).times(Variable.of("a32")).times(IntegerConstant.MINUS_ONE)
        ).simplify();
        Assert.assertEquals(result, A.det().simplify());
    }
}

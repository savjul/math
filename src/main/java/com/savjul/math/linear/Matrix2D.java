package com.savjul.math.linear;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.IntegerConstant;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Matrix2D {
    private final Expression[][] matrix;

    private Matrix2D(Expression[][] matrix) {
        this.matrix = matrix;
    }

    public static Matrix2D getI(int n) {
        Expression[][] matrix = new Expression[n][];
        for (int idx = 0; idx < n; idx++) {
            Expression[] row = new Expression[n];
            for (int jdx = 0; jdx < n; jdx++) {
                row[jdx] = jdx == idx ? IntegerConstant.ONE : IntegerConstant.ZERO;
            }
            matrix[idx] = row;
        }
        return new Matrix2D(matrix);
    }

    public static Matrix2D of(Expression[][] matrix) {
        matrix = Arrays.copyOf(matrix, matrix.length);
        for (int idx = 0; idx < matrix.length; idx++) {
            matrix[idx] = Arrays.copyOf(matrix[idx], matrix[idx].length);
        }
        return new Matrix2D(matrix);
    }

    public Expression get(int i, int j) {
        return this.matrix[i][j];
    }

    public Matrix2D plus(Matrix2D o) {
        Expression[][] matrix = new Expression[this.matrix.length][];
        for (int idx = 0; idx < matrix.length; idx++) {
            Expression[] row = new Expression[this.matrix[idx].length];
            for (int jdx = 0; jdx < row.length; jdx++) {
                row[jdx] = this.matrix[idx][jdx].plus(o.matrix[idx][jdx]);
            }
            matrix[idx] = row;
        }
        return new Matrix2D(matrix);
    }

    public Matrix2D times(Expression scalar) {
        Expression[][] matrix = new Expression[this.matrix.length][];
        for (int idx = 0; idx < matrix.length; idx++) {
            Expression[] row = new Expression[this.matrix[idx].length];
            for (int jdx = 0; jdx < row.length; jdx++) {
                row[jdx] = this.matrix[idx][jdx].times(scalar);
            }
            matrix[idx] = row;
        }
        return new Matrix2D(matrix);
    }

    public Matrix2D times(Matrix2D o) {
        Expression[][] matrix = new Expression[this.matrix.length][];
        for (int idx = 0; idx < matrix.length; idx++) {
            Expression[] row = new Expression[o.matrix[0].length];
            for (int jdx = 0; jdx < row.length; jdx++) {
                Expression r = IntegerConstant.ZERO;
                for (int rdx = 0; rdx < this.matrix[0].length; rdx++) {
                    r = r.plus(this.matrix[idx][rdx].times(o.matrix[rdx][jdx]));
                }
                row[jdx] = r;
            }
            matrix[idx] = row;
        }
        return new Matrix2D(matrix);
    }

    public Vector times(Vector vector) {
        Expression[] result = new Expression[this.matrix.length];
        Expression[] v = vector.getValues();
        for (int idx = 0; idx < result.length; idx++) {
            Expression[] row = this.matrix[idx];
            Expression r = IntegerConstant.ZERO;
            for (int jdx = 0; jdx < row.length; jdx++) {
                r = r.plus(row[jdx].times(v[jdx]));
            }
            result[idx] = r;
        }
        return Vector.of(result);
    }

    @Override
    public String toString() {
        return "[" + String.join(", ", Stream.of(matrix)
                .map(v->"[" + String.join(", ", Stream.of(v).map(Expression::toString)
                        .collect(Collectors.toList())) + "]")
                .collect(Collectors.toList())) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Matrix2D matrix2D = (Matrix2D) o;

        return Arrays.deepEquals(matrix, matrix2D.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }
}

package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.Constant;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

public final class Trigonometric extends AbstractBaseExpression {
    private enum Function {
        sin(Math::sin) {
            @Override
            Function inverse() {
                return csc;
            }
        },
        cos(Math::cos) {
            @Override
            Function inverse() {
                return sec;
            }
        },
        tan(Math::tan) {
            @Override
            Function inverse() {
                return cot;
            }
        },
        cot(x -> 1.0 / Math.tan(x)) {
            @Override
            Function inverse() {
                return tan;
            }
        },
        sec(x -> 1.0 / Math.cos(x)) {
            @Override
            Function inverse() {
                return cos;
            }
        },
        csc(x -> 1.0 / Math.sin(x)) {
            @Override
            Function inverse() {
                return sin;
            }
        };

        Function(DoubleUnaryOperator doubleOp) {
            this.doubleOp = doubleOp;
        }

        abstract Function inverse();
        final DoubleUnaryOperator doubleOp;
    }

    private final Function function;
    private final Expression argument;

    public static Trigonometric sin(Expression argument) {
        return new Trigonometric(Function.sin, argument);
    }

    public static Trigonometric cos(Expression argument) {
        return new Trigonometric(Function.cos, argument);
    }

    public static Trigonometric tan(Expression argument) {
        return new Trigonometric(Function.tan, argument);
    }

    private Trigonometric(Function function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    public String getName() {
        return function.name();
    }

    public Expression getArgument() {
        return argument;
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    @Override
    public boolean isConstant() {
        return argument.isConstant();
    }

    @Override
    public Trigonometric invert() {
        return new Trigonometric(function.inverse(), argument);
    }

    public double doubleValue() {
        if (argument instanceof Constant) {
            return function.doubleOp.applyAsDouble(((Constant) argument).getValue().doubleValue());
        }
        else {
            throw new IllegalArgumentException("Can't calculate value of " + argument.toString());
        }
    }

    public Trigonometric withArgument(Expression argument) {
        return new Trigonometric(this.function, argument);
    }

    public Trigonometric inverseWithArgument(Expression argument) {
        return new Trigonometric(this.function.inverse(), argument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigonometric that = (Trigonometric) o;
        return function == that.function &&
                Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, argument);
    }
}

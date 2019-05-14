package com.savjul.math.expression.compound;

import com.savjul.math.expression.AbstractBaseExpression;
import com.savjul.math.expression.Expression;
import com.savjul.math.transformers.Calculator;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

public final class Trigonometric extends AbstractBaseExpression {
    private enum Type {
        sin(Math::sin) {
            @Override
            Type inverse() {
                return csc;
            }
        },
        cos(Math::cos) {
            @Override
            Type inverse() {
                return sec;
            }
        },
        tan(Math::tan) {
            @Override
            Type inverse() {
                return cot;
            }
        },
        cot(x -> 1.0 / Math.tan(x)) {
            @Override
            Type inverse() {
                return tan;
            }
        },
        sec(x -> 1.0 / Math.cos(x)) {
            @Override
            Type inverse() {
                return cos;
            }
        },
        csc(x -> 1.0 / Math.sin(x)) {
            @Override
            Type inverse() {
                return sin;
            }
        };

        Type(DoubleUnaryOperator doubleOp) {
            this.doubleOp = doubleOp;
        }

        abstract Type inverse();
        final DoubleUnaryOperator doubleOp;
    }

    private final Type type;
    private final Expression argument;

    public static Trigonometric sin(Expression argument) {
        return new Trigonometric(Type.sin, argument);
    }

    public static Trigonometric cos(Expression argument) {
        return new Trigonometric(Type.cos, argument);
    }

    public static Trigonometric tan(Expression argument) {
        return new Trigonometric(Type.tan, argument);
    }

    private Trigonometric(Type type, Expression argument) {
        this.type = type;
        this.argument = argument;
    }

    public String getName() {
        return type.name();
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
        return new Trigonometric(type.inverse(), argument);
    }

    public double doubleValue() {
        return doubleValue(Calculator::doubleValue);
    }

    public double doubleValue(Function<Expression, Double> calc) {
        // a bit circular but oh well
        return type.doubleOp.applyAsDouble(calc.apply(argument));
    }

    public Trigonometric withArgument(Expression argument) {
        return new Trigonometric(this.type, argument);
    }

    public Trigonometric inverseWithArgument(Expression argument) {
        return new Trigonometric(this.type.inverse(), argument);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trigonometric that = (Trigonometric) o;
        return type == that.type &&
                Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, argument);
    }
}

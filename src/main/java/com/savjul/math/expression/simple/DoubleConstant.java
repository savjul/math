package com.savjul.math.expression.simple;

public final class DoubleConstant extends Constant<Double> {

    public static DoubleConstant of(Double value) {
        return new DoubleConstant(value);
    }

    private DoubleConstant(Double value) {
        super(value);
    }
}

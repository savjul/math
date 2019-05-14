package com.savjul.math.expression.simple;

public final class IntegerConstant extends Constant<Long> {

    private IntegerConstant(Long value) {
        super(value);
    }

    public static IntegerConstant of(Long value) {
        return new IntegerConstant(value);
    }

    public static IntegerConstant of(Integer value) {
        return new IntegerConstant(value.longValue());
    }
}

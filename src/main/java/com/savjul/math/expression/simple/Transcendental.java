package com.savjul.math.expression.simple;

import com.savjul.math.expression.Expression;

public final class Transcendental extends Constant<Double> {
    private final String name;

    public static final Expression E = new Transcendental("e", Math.E);
    public static final Expression PI = new Transcendental("π", Math.PI);

    private Transcendental(String name, double value) {
        super(value);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}

package com.savjul.math.expression;

import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Polynomial;
import com.savjul.math.expression.compound.Rational;
import com.savjul.math.expression.compound.Term;
import com.savjul.math.expression.simple.IntegerConstant;
import com.savjul.math.transformers.RenderingVisitor;
import com.savjul.math.transformers.VisitingSimplifier;

public abstract class AbstractBaseExpression implements Expression {
    @Override
    public Expression plus(Expression o) {
        return Polynomial.of(this, o);
    }

    @Override
    public Expression times(Expression o) {
        return Term.of(this, o);
    }

    @Override
    public Expression divideBy(Expression o) {
        return Rational.of(this, o);
    }

    @Override
    public Expression invert() {
        return Rational.of(IntegerConstant.ONE, this);
    }

    @Override
    public Expression pow(Expression o) {
        return Exponent.of(this, o);
    }

    @Override
    public Expression simplify() {
        return this.apply(VisitingSimplifier.instance());
    }

    @Override
    public boolean isCompound() {
        return false;
    }

    @Override
    public String toString() {
        return RenderingVisitor.instance().apply(this);
    }
}

package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.simple.Constant;
import com.savjul.math.expression.simple.Variable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class VariableExpander extends ExpressionTransformer {
    private final Map<String, Expression> bindings;
    public static Builder get() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<String, Expression> mapping;

        private Builder() {
            mapping = new LinkedHashMap<>();
        }

        public Builder add(String name, Expression value) {
            this.mapping.put(name, value);
            return this;
        }

        public Builder add(String name, int value) {
            this.mapping.put(name, Constant.of(value));
            return this;
        }

        public Builder add(String name, long value) {
            this.mapping.put(name, Constant.of(value));
            return this;
        }

        public Builder add(String name, double value) {
            this.mapping.put(name, Constant.of(value));
            return this;
        }

        public Function<Expression, Expression> build() {
            return expression -> expand(mapping, expression);
        }
    }

    private VariableExpander(Map<String, Expression> bindings) {
        this.bindings = bindings;
    }

    private static Expression expand(Map<String, Expression> bindings, Expression expression) {
        return new VariableExpander(bindings).visit(expression);
    }

    @Override
    public Expression visit(Variable expression) {
        return bindings.getOrDefault((expression).getName(), expression);
    }
}

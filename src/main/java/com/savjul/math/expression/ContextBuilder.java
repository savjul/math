package com.savjul.math.expression;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ContextBuilder {
    private final Map<String, Expression> mapping;

    private ContextBuilder() {
        mapping = new LinkedHashMap<>();
    }

    public static ContextBuilder get() {
        return new ContextBuilder();
    }

    public ContextBuilder add(String name, Expression value) {
        this.mapping.put(name, value);
        return this;
    }

    public ContextBuilder add(String name, int value) {
        this.mapping.put(name, IntegerConstant.of(value));
        return this;
    }

    public Context build() {
        return new Context() {
            private final Map<String, Expression> m = mapping;
            @Override
            public Expression getValue(String name, Expression defaultValue) {
                return m.getOrDefault(name, defaultValue);
            }
        };
    }
}

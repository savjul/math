package com.savjul.math.transformers;

import com.savjul.math.expression.Expression;
import com.savjul.math.expression.compound.Exponent;
import com.savjul.math.expression.compound.Term;
import com.savjul.math.expression.simple.NumericConstant;
import com.savjul.math.expression.simple.Variable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class BasicComparison {
    private static final Comparator<Expression> FACTOR_COMPARATOR = new DisplayComparator(true);
    private static final Comparator<Expression> TERM_COMPARATOR = new DisplayComparator(false);

    public static Comparator<Expression> factors() {
        return FACTOR_COMPARATOR;
    }

    public static Comparator<Expression> terms() {
        return TERM_COMPARATOR;
    }

    private static final class DisplayComparator implements Comparator<Expression> {
        private final boolean constantFirst;

        private DisplayComparator(boolean constantFirst) {
            this.constantFirst = constantFirst;
        }

        @Override
        public int compare(Expression o1, Expression o2) {
            if (isNumeric(o1) && isNumeric(o2)) {
                return compareNumericConstants(o2, o1);
            } else if (isNumeric(o1)) {
                return constantFirst ? -1 : 1;
            }
            else if (isNumeric(o2)) {
                return constantFirst ? 1 : -1;
            }
            else if (o1 instanceof Term && o2 instanceof Term) {
                return BasicComparison.compare(((Term)o1).getFactors(false), ((Term)o2).getFactors(false), this);
            }
            else if (o1 instanceof Term && !o2.isConstant()) {
                return BasicComparison.compare(((Term)o1).getFactors(false), Collections.singletonList(o2), this);
            }
            else if (o2 instanceof Term && !o1.isConstant()) {
                return BasicComparison.compare(Collections.singletonList(o1), ((Term)o2).getFactors(false), this);
            }
            else if (o1 instanceof Variable && o2 instanceof Variable) {
                return ((Variable)o1).getName().compareTo((((Variable) o2).getName()));
            }
            else if (o1 instanceof Exponent && o2 instanceof Exponent) {
                Exponent e1 = (Exponent) o1; Exponent e2 = (Exponent) o2;
                int res = 0;
                if (e1.getBase().getClass() == e2.getBase().getClass()) {
                    res = this.compare(e1.getPower(), e2.getPower());
                }
                if (res == 0) {
                    res = this.compare(e1.getBase(), e2.getBase());
                }
                return res;
            }
            else if (o1 instanceof Exponent) {
                return -1;
            }
            else if (o2 instanceof Exponent) {
                return 1;
            }
            return 0;
        }
    }

    private static boolean isNumeric(Expression o1) {
        return o1 instanceof NumericConstant;
    }

    private static int compareNumericConstants(Expression o1, Expression o2) {
        return Double.compare(((NumericConstant)o1).getValue().doubleValue(), ((NumericConstant)o2).getValue().doubleValue());
    }

    private static int compare(List<Expression> l1, List<Expression> l2, Comparator<Expression> comparator) {
        int min = Math.min(l1.size(), l2.size());
        int res;
        res = Integer.compare(l2.size(), l1.size());
        if (res != 0) {
            return res;
        }
        for (int idx = 0; idx < min; idx++) {
            res = comparator.compare(l1.get(idx), l2.get(idx));
            if (res != 0) {
                return res;
            }
        }
        return 0;
    }
}

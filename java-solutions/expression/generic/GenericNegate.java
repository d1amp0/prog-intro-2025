package expression.generic;

public class GenericNegate<T extends Number> implements GenericTripleExpression<T> {
    private final GenericTripleExpression<T> exp;
    private final GenericOperations<T> calculator;

    public GenericNegate(GenericTripleExpression<T> exp, GenericOperations<T> calculator) {
        this.exp = exp;
        this.calculator = calculator;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return this.calculator.negate(exp.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "-(" + exp.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GenericNegate<?> other = (GenericNegate<?>) obj;
        return exp.equals(other.exp);
    }

    @Override
    public int hashCode() {
        return exp.hashCode() * 31 + "-".hashCode();
    }
}
package expression.generic;

public class GenericCount<T extends Number> implements GenericTripleExpression<T> {
    private final GenericTripleExpression<T> exp;
    private final GenericOperations<T> calculator;

    public GenericCount(GenericTripleExpression<T> exp, GenericOperations<T> calculator) {
        this.exp = exp;
        this.calculator = calculator;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return this.calculator.count(exp.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "count " + exp.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GenericCount<?> other = (GenericCount<?>) obj;
        return exp.equals(other.exp);
    }

    @Override
    public int hashCode() {
        return exp.hashCode() * 31 + "count".hashCode();
    }
}
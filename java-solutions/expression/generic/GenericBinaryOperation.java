package expression.generic;

public abstract class GenericBinaryOperation<T extends Number> implements GenericTripleExpression<T> {
    protected final GenericTripleExpression<T> first, second;
    protected final GenericOperations<T> calculator;

    public GenericBinaryOperation(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                                  GenericOperations<T> calculator) {
        this.first = first;
        this.second = second;
        this.calculator = calculator;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return calc(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    protected abstract T calc(T a, T b);

    protected abstract String getSign();

    @Override
    public String toString() {
        return "(" + first.toString() + " " + getSign() + " " + second.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        GenericBinaryOperation<?> operation = (GenericBinaryOperation<?>) obj;
        return first.equals(operation.first) && second.equals(operation.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        result = 31 * result + getClass().hashCode();
        return result;
    }
}
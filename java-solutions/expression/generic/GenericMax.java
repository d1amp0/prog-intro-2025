package expression.generic;

public class GenericMax<T extends Number> extends GenericBinaryOperation<T> {
    public GenericMax(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                      GenericOperations<T> calculator) {
        super(first, second, calculator);
    }

    @Override
    protected T calc(T a, T b) {
        return this.calculator.max(a, b);
    }

    @Override
    protected String getSign() {
        return "max";
    }
}
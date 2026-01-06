package expression.generic;

public class GenericMultiply<T extends Number> extends GenericBinaryOperation<T> {
    public GenericMultiply(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                      GenericOperations<T> calculator) {
        super(first, second, calculator);
    }

    @Override
    protected T calc(T a, T b) {
        return this.calculator.multiply(a, b);
    }

    @Override
    protected String getSign() {
        return "*";
    }
}
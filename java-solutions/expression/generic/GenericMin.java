package expression.generic;

public class GenericMin<T extends Number> extends GenericBinaryOperation<T> {
    public GenericMin(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                      GenericOperations<T> calculator) {
        super(first, second, calculator);
    }

    @Override
    protected T calc(T a, T b) {
        return this.calculator.min(a, b);
    }

    @Override
    protected String getSign() {
        return "min";
    }
}
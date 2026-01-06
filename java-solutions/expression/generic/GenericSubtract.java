package expression.generic;

public class GenericSubtract<T extends Number> extends GenericBinaryOperation<T> {
    public GenericSubtract(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                      GenericOperations<T> calculator) {
        super(first, second, calculator);
    }

    @Override
    protected T calc(T a, T b) {
        return this.calculator.subtract(a, b);
    }

    @Override
    protected String getSign() {
        return "-";
    }
}
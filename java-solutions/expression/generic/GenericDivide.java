package expression.generic;

public class GenericDivide<T extends Number> extends GenericBinaryOperation<T> {
    public GenericDivide(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                      GenericOperations<T> calculator) {
        super(first, second, calculator);
    }

    @Override
    protected T calc(T a, T b) {
        return this.calculator.divide(a, b);
    }

    @Override
    protected String getSign() {
        return "/";
    }
}
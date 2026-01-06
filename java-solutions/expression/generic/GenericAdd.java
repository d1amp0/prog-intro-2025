package expression.generic;

public class GenericAdd<T extends Number> extends GenericBinaryOperation<T> {
    public GenericAdd(GenericTripleExpression<T> first, GenericTripleExpression<T> second,
                      GenericOperations<T> calculator) {
        super(first, second, calculator);
    }

    @Override
    protected T calc(T a, T b) {
        return this.calculator.add(a, b);
    }

    @Override
    protected String getSign() {
        return "+";
    }
}
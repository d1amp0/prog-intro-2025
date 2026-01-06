package expression;

public class Multiply extends BinaryOperation {
    public Multiply(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        return a * b;
    }

    @Override
    protected double calc(double a, double b) {
        return a * b;
    }

    @Override
    protected String getSign() {
        return "*";
    }
}
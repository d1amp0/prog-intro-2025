package expression;

public class Or extends BinaryOperation {
    public Or(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        return a | b;
    }

    @Override
    protected double calc(double a, double b) {
        throw new IllegalArgumentException("Incorrect operation or for double");
    }

    @Override
    protected String getSign() {
        return "|";
    }
}
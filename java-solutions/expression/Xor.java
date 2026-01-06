package expression;

public class Xor extends BinaryOperation {
    public Xor(AnyExpression first, AnyExpression second) {
        super(first, second);
    }

    @Override
    protected int calc(int a, int b) {
        return a ^ b;
    }

    @Override
    protected double calc(double a, double b) {
        throw new IllegalArgumentException("Incorrect operation xor for double");
    }

    @Override
    protected String getSign() {
        return "^";
    }
}
package expression;

public abstract class BinaryOperation implements AnyExpression {
    protected final AnyExpression first, second;

    public BinaryOperation(AnyExpression first, AnyExpression second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int evaluate(int value) {
        return calc(first.evaluate(value), second.evaluate(value));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return calc(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    @Override
    public double evaluateD(double x, double y, double z) {
        return calc(first.evaluateD(x, y, z), second.evaluateD(x, y, z));
    }

    protected abstract int calc(int a, int b);

    protected abstract double calc(double a, double b);

    protected abstract String getSign();

    @Override
    public String toString() {
        return "(" + first.toString() + " " + getSign() + " " + second.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        BinaryOperation operation = (BinaryOperation) obj;
        return first.equals(operation.first) && second.equals(operation.second);
    }

    @Override
    public int hashCode() {
        int result = first.hashCode();
        result = 31 * result + second.hashCode();
        result = 31 * result + getClass().hashCode();
        return result;
    }

    public AnyExpression getFirst() {
        return first;
    }

    public AnyExpression getSecond() {
        return second;
    }
}
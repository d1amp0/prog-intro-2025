package expression.generic;

public class DoubleOperations implements GenericOperations<Double> {

    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double negate(Double a) {
        return -a;
    }

    @Override
    public Double parseConst(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Double count(Double a) {
        return (double) Long.bitCount(Double.doubleToLongBits(a));
    }

    @Override
    public Double min(Double a, Double b) {
        return Double.min(a, b);
    }

    @Override
    public Double max(Double a, Double b) {
        return Double.max(a, b);
    }

    @Override
    public Double valueOf(int value) {
        return (double) value;
    }
}

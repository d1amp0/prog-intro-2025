package expression.generic;

public class IntegerNoCheckOperations implements GenericOperations<Integer> {
    @Override
    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        return a / b;
    }

    @Override
    public Integer negate(Integer a) {
        return -a;
    }

    @Override
    public Integer parseConst(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Integer.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Integer.max(a, b);
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }
}

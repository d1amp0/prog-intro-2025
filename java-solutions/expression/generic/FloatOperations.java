package expression.generic;

public class FloatOperations implements GenericOperations<Float> {

    @Override
    public Float add(Float a, Float b) {
        return a + b;
    }

    @Override
    public Float subtract(Float a, Float b) {
        return a - b;
    }

    @Override
    public Float multiply(Float a, Float b) {
        return a * b;
    }

    @Override
    public Float divide(Float a, Float b) {
        return a / b;
    }

    @Override
    public Float negate(Float a) {
        return -a;
    }

    @Override
    public Float parseConst(String value) {
        return Float.valueOf(value);
    }

    @Override
    public Float count(Float a) {
        return (float) Integer.bitCount(Float.floatToIntBits(a));
    }

    @Override
    public Float min(Float a, Float b) {
        return Float.min(a, b);
    }

    @Override
    public Float max(Float a, Float b) {
        return Float.max(a, b);
    }

    @Override
    public Float valueOf(int value) {
        return (float)value;
    }
}

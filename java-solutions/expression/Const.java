package expression;

import java.util.Objects;

public class Const implements AnyExpression {
    private final Number value;

    public Const(int value) {
        this.value = value;
    }

    public Const(double value) {
        this.value = value;
    }

    @Override
    public int evaluate(int value) {
        return this.value.intValue();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return this.value.intValue();
    }

    @Override
    public double evaluateD(double x, double y, double z) {
        return this.value.doubleValue();
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Const con)) return false;
        return value.equals(con.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, getClass());
    }
}
package expression;

public class Negate implements AnyExpression {
    protected final AnyExpression exp;

    public Negate(AnyExpression exp) {
        this.exp = exp;
    }

    @Override
    public int evaluate(int value) {
        return -exp.evaluate(value);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return -exp.evaluate(x, y, z);
    }

    @Override
    public double evaluateD(double x, double y, double z) {
        return -exp.evaluateD(x, y, z);
    }

    @Override
    public String toString() {
        return "-(" + exp.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Negate other = (Negate) obj;
        return exp.equals(other.exp);
    }

    @Override
    public int hashCode() {
        return exp.hashCode() * 31 + "-".hashCode();
    }
}
package expression;

public class Variable implements AnyExpression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int evaluate(int value) {
        return value;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return switch (name) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> -1;
        };
    }

    @Override
    public double evaluateD(double x, double y, double z) {
        return switch (name) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> -1;
        };
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Variable var)) return false;
        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
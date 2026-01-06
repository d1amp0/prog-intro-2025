package expression.generic;

public class GenericVariable<T extends Number> implements GenericTripleExpression<T> {
    private final String name;

    public GenericVariable(String name) {
        this.name = name;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return switch (name) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> throw new IllegalArgumentException("Wrong variable");
        };
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof GenericVariable<?> var)) return false;
        return name.equals(var.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
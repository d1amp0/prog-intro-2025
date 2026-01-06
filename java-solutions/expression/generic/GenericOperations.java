package expression.generic;

public interface GenericOperations<T extends Number> {
    T add(T a, T b);
    T subtract(T a, T b);
    T multiply(T a, T b);
    T divide(T a, T b);
    T negate(T a);
    T parseConst(String value);
    T count(T a);
    T min(T a, T b);
    T max(T a, T b);
    T valueOf(int value);
}

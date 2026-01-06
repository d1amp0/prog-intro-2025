package base;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record Named<T>(String name, T value) {
    public static <T> Named<T> of(final String name, final T f) {
        return new Named<>(name, f);
    }

    @Override
    public String toString() {
        return name;
    }
}

package base;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "unused"})
public record Pair<F, S>(F first, S second) {
    public static <F, S> Pair<F, S> of(final F first, final S second) {
        return new Pair<>(first, second);
    }

    public static <F, S> Pair<F, S> of(final Map.Entry<F, S> e) {
        return of(e.getKey(), e.getValue());
    }

    public static <F, S> UnaryOperator<Pair<F, S>> lift(final UnaryOperator<F> f, final UnaryOperator<S> s) {
        return p -> of(f.apply(p.first), s.apply(p.second));
    }

    public static <F, S> BinaryOperator<Pair<F, S>> lift(final BinaryOperator<F> f, final BinaryOperator<S> s) {
        return (p1, p2) -> of(f.apply(p1.first, p2.first), s.apply(p1.second, p2.second));
    }

    public static <T, F, S> Function<T, Pair<F, S>> tee(
            final Function<? super T, ? extends F> f,
            final Function<? super T, ? extends S> s
    ) {
        return t -> of(f.apply(t), s.apply(t));
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    public <R> Pair<F, R> second(final R second) {
        return new Pair<>(first, second);
    }
}

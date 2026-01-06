package expression.common;

import base.Functional;
import base.Pair;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public record Expr<C, V>(Node<C> node, List<Pair<String, V>> variables) {
    public <T> List<Pair<String, T>> variables(final BiFunction<String, V, T> f) {
        return Functional.map(
                variables,
                variable -> variable.second(f.apply(variable.first(), variable.second()))
        );
    }

    public <T> Expr<C, T> convert(final BiFunction<String, V, T> f) {
        return of(node, variables(f));
    }

    public Expr<C, V> node(final Function<Node<C>, Node<C>> f) {
        return of(f.apply(node), variables);
    }

    public static <C, V> Expr<C, V> of(final Node<C> node, final List<Pair<String, V>> variables) {
        return new Expr<>(node, variables);
    }
}

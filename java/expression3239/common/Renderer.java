package expression.common;

import base.Functional;
import base.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Renderer<C, S, R> {
    static <C, S, R> Builder<C, S, R> builder(final Node.Const<C, R> constant) {
        return new Builder<>(constant);
    }

    R render(final Expr<C, R> expr, final S settings);

    @FunctionalInterface
    interface UnaryOperator<S, R> {
        R apply(S settings, R arg);
    }

    @FunctionalInterface
    interface BinaryOperator<S, R> {
        R apply(S settings, R arg1, R arg2);
    }

    final class Builder<C, S, R> {
        private final Node.Const<C, R> constant;
        private final Map<String, UnaryOperator<S, R>> unary = new HashMap<>();
        private final Map<String, BinaryOperator<S, R>> binary = new HashMap<>();

        private Builder(final Node.Const<C, R> constant) {
            this.constant = constant;
        }

        public void unary(final String name, final UnaryOperator<S, R> op) {
            unary.put(name, op);
        }

        public void binary(final String name, final BinaryOperator<S, R> op) {
            binary.put(name, op);
        }

        public Renderer<C, S, R> build() {
            return (expr, settings) -> {
                final Map<String, R> vars = expr.variables().stream()
                        .collect(Collectors.toMap(Pair::first, Pair::second));
                return expr.node().cata(
                        constant,
                        name -> Functional.get(vars, name),
                        (name, p, arg) -> Functional.get(unary, name).apply(settings, arg),
                        (name, arg1, arg2) -> Functional.get(binary, name).apply(settings, arg1, arg2)
                );
            };
        }
    }
}

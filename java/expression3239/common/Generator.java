package expression.common;

import base.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Generator<C, E> {
    private final Supplier<C> constant;
    private final List<Named<Integer>> ops;
    private final ExpressionKind.Variables<E> variables;
    private final Set<String> forbidden;
    private final ExtendedRandom random;
    private final List<Function<List<Node<C>>, Stream<Node<C>>>> basicTests;

    public Generator(
            final Supplier<C> constant,
            final List<Named<Integer>> ops,
            final ExpressionKind.Variables<E> variables,
            final Set<String> forbidden,
            final ExtendedRandom random,
            final List<Function<List<Node<C>>, Stream<Node<C>>>> basicTests
    ) {
        this.constant = constant;
        this.ops = List.copyOf(ops);
        this.variables = variables;
        this.forbidden = Set.copyOf(forbidden);
        this.random = random;
        this.basicTests = List.copyOf(basicTests);
    }

    public static <C> Builder<C> builder(final Supplier<C> constant, final ExtendedRandom random) {
        return new Builder<>(random, constant);
    }

    public void testRandom(
            final TestCounter counter,
            final int denominator,
            final Consumer<Expr<C, E>> consumer
    ) {
        final int d = Math.max(TestCounter.DENOMINATOR, denominator);
        testRandom(counter, consumer, 1, 100, 100 / d, (vars, depth) -> generateFullDepth(vars, Math.min(depth, 3)));
        testRandom(counter, consumer, 2, 1000 / d, 1, this::generateSize);
        testRandom(counter, consumer, 3, 12, 100 / d, this::generateFullDepth);
        testRandom(counter, consumer, 4, 777 / d, 1, this::generatePartialDepth);
    }

    private void testRandom(
            final TestCounter counter,
            final Consumer<Expr<C, E>> consumer,
            final int seq,
            final int levels,
            final int perLevel,
            final BiFunction<List<Node<C>>, Integer, Node<C>> generator
    ) {
        counter.scope("Random tests #" + seq, () -> {
            final int total = levels * perLevel;
            int generated = 0;
            for (int level = 0; level < levels; level++) {
                for (int j = 0; j < perLevel; j++) {
                    if (generated % 100 == 0) {
                        progress(counter, total, generated);
                    }
                    generated++;

                    final List<Pair<String, E>> vars = variables(random.nextInt(10) + 1);
                    consumer.accept(Expr.of(generator.apply(Functional.map(vars, v -> Node.op(v.first())), level), vars));
                }
            }
            progress(counter, generated, total);
        });
    }

    private static void progress(final TestCounter counter, final int total, final int generated) {
        counter.format("Completed %4d out of %d%n", generated, total);
    }

    private Node<C> generate(
            final List<Node<C>> variables,
            final boolean nullary,
            final Supplier<Node<C>> unary,
            final Supplier<Pair<Node<C>, Node<C>>> binary
    ) {
        if (nullary || ops.isEmpty()) {
            return random.nextBoolean() ? random.randomItem(variables) : Node.constant(constant.get());
        } else {
            final Named<Integer> op = random.randomItem(ops);
            if (Math.abs(op.value()) == 1) {
                return Node.op(op.name(), (op.value() + 1) >> 1, unary.get());
            } else {
                final Pair<Node<C>, Node<C>> pair = binary.get();
                return Node.op(op.name(), pair.first(), pair.second());
            }
        }
    }

    private Node<C> generate(final List<Node<C>> variables, final boolean nullary, final Supplier<Node<C>> child) {
        return generate(variables, nullary, child, () -> Pair.of(child.get(), child.get()));
    }

    private Node<C> generateFullDepth(final List<Node<C>> variables, final int depth) {
        return generate(variables, depth == 0, () -> generateFullDepth(variables, depth - 1));
    }

    private Node<C> generatePartialDepth(final List<Node<C>> variables, final int depth) {
        return generate(variables, depth == 0, () -> generatePartialDepth(variables, random.nextInt(depth)));
    }

    private Node<C> generateSize(final List<Node<C>> variables, final int size) {
        final int first = size <= 1 ? 0 : random.nextInt(size);
        return generate(
                variables,
                size == 0,
                () -> generateSize(variables, size - 1),
                () -> Pair.of(
                        generateSize(variables, first),
                        generateSize(variables, size - 1 - first)
                )
        );
    }

    public void testBasic(final Consumer<Expr<C, E>> consumer) {
        basicTests.forEach(test -> {
            final List<Pair<String, E>> vars = variables(random.nextInt(5) + 3);
            test.apply(Functional.map(vars, v -> Node.op(v.first())))
                    .map(node -> Expr.of(node, vars))
                    .forEachOrdered(consumer);
        });
    }

    public List<Pair<String, E>> variables(final int count) {
        List<Pair<String, E>> vars;
        do {
            vars = variables.generate(random, count);
        } while (vars.stream().map(Pair::first).anyMatch(forbidden::contains));
        return vars;
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public static final class Builder<C> {
        private final ExtendedRandom random;
        private final Supplier<C> constant;

        private final List<Named<Integer>> ops = new ArrayList<>();
        private final Set<String> forbidden = new HashSet<>();

        private Builder(final ExtendedRandom random, final Supplier<C> constant) {
            this.random = random;
            this.constant = constant;
        }

        public void add(final String name, final int arity) {
            ops.add(Named.of(name, arity));
            forbidden.add(name);
        }

        public <E> Generator<C, E> build(
                final ExpressionKind.Variables<E> variables,
                final List<Function<List<Node<C>>, Stream<Node<C>>>> basicTests
        ) {
            return new Generator<>(constant, ops, variables, forbidden, random, basicTests);
        }
    }
}

package expression.generic;

import base.*;
import expression.ToMiniString;
import expression.common.*;
import expression.parser.ParserTestSet;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class GenericTester extends Tester {
    private static final int SIZE = 10;
    private static final int MAX = Integer.MAX_VALUE - 1;
    private static final int MIN = Integer.MIN_VALUE;
    private static final List<Pair<String, F<?>>> VARIABLES = List.of(
            Pair.of("x", (x, y, z) -> x),
            Pair.of("y", (x, y, z) -> y),
            Pair.of("z", (x, y, z) -> z)
    );
    private static final ExpressionKind.Variables<F<?>> VARS = (c, r) -> VARIABLES;

    protected final List<Named<IF<?>>> tests = new ArrayList<>();
    private final Tabulator tabulator = new GenericTabulator();
    private final Set<String> operations = new HashSet<>();
    private final List<NodeRenderer.Paren> parens = new ArrayList<>(List.of(NodeRenderer.paren("(", ")")));

    private final TestGeneratorBuilder<Integer> generator;
    private final List<Mode.Builder<?>> modes = new ArrayList<>();

    public GenericTester(final TestCounter counter) {
        super(counter);
        generator = new TestGeneratorBuilder<>(
                random(),
                random()::nextInt,
                ParserTestSet.CONSTS,
                false
        );
    }

    protected void test(final String expression, final String name, final IF<?> f) {
        tests.add(Named.of(name + ": " + expression, f));
    }

    @Override
    public void test() {
        final List<Mode> modes = this.modes.stream()
                .map(mode -> mode.build(this))
                .toList();

        for (final Named<IF<?>> test : tests) {
            final String[] parts = test.name().split(": ");
            testFull(parts[0], parts[1], test.value());
        }

        final TestGenerator<Integer, F<?>> generator = this.generator.build(VARS);
        counter.scope("basic", () -> generator.testBasic(test(true, modes)));
        counter.scope("random", () -> generator.testRandom(
                counter,
                20 + (TestCounter.DENOMINATOR - 1) * 2,
                test(false, modes)
        ));
    }

    private void testFull(final String mode, final String expression, final IF<?> f) {
        testShort(mode, expression, f);
        test(mode, expression, f, MAX, -1, MAX, 0);
        test(mode, expression, f, MIN, 0, MIN, 1);
    }

    private void testShort(final String mode, final String expression, final IF<?> f) {
        test(mode, expression, f, 0, -1, 0, 1);
    }

    private void test(final String mode, final String expression, final IF<?> f, final int min, final int dMin, final int max, final int dMax) {
        test(
                mode, expression, f,
                min + random().nextInt(SIZE) * dMin, max + random().nextInt(SIZE) * dMax,
                min + random().nextInt(SIZE) * dMin, max + random().nextInt(SIZE) * dMax,
                min + random().nextInt(SIZE) * dMin, max + random().nextInt(SIZE) * dMax
        );
    }

    private Consumer<TestGenerator.Test<Integer, F<?>>> test(final boolean full, final List<Mode> modes) {
        final NodeRenderer.Settings settings = NodeRenderer.FULL.withParens(parens);
        return test -> modes
                .forEach(mode -> mode.test(test.expr, test.render(settings), full));
    }

    private <T> void test(final String mode, final String expression, final IF<T> f, final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) {
        final String context = String.format("mode=%s, x=[%d, %d] y=[%d, %d] z=[%d, %d], expression=%s%n", mode, x1, x2, y1, y2, z1, z2, expression);
        final Object[][][] result = counter.testV(() -> TestCounter.get(() -> tabulator.tabulate(mode, expression, x1, x2, y1, y2, z1, z2))
                .either(e -> counter.fail(e, "%s %s", "tabulate", context), Function.identity()));
        IntStream.rangeClosed(x1, x2).forEach(x ->
                IntStream.rangeClosed(y1, y2).forEach(y ->
                        IntStream.rangeClosed(z1, z2).forEach(z ->
                                counter.test(() -> {
                                    final Object expected = TestCounter.get(() -> f.apply(x, y, z)).either(e -> null, Function.identity());
                                    final Object actual = result[x - x1][y - y1][z - z1];
                                    counter.checkTrue(
                                            Objects.equals(actual, expected),
                                            "table[%d][%d][%d](x=%d, y=%d, z=%d]) = %s (expected %s)%n%s",
                                            x - x1, y - y1, z - z1,
                                            x, y, z,
                                            actual, expected, context
                                    );
                                }))));
    }

    public void binary(final String name, final int priority) {
        operations.add(name + ":2");
        generator.binary(name, priority);
    }

    public void unary(final String name, final int priority) {
        operations.add(name + ":1");
        generator.unary(name, priority);
    }

    public void parens(final String... parens) {
        assert parens.length % 2 == 0 : "Parens should come in pairs";
        for (int i = 0; i < parens.length; i += 2) {
            this.parens.add(NodeRenderer.paren(parens[i], parens[i + 1]));
        }
    }

    /* package-private */ interface Mode {
        static <T> Builder<T> builder(
                final String mode,
                final IntFunction<T> constant,
                final IntUnaryOperator fixer
        ) {
            return new Builder<>(mode, constant, fixer);
        }

        void test(final Expr<Integer, ? extends F<?>> expr, final String expression, final boolean full);

        /* package-private */ final class Builder<T> implements Consumer<GenericTester> {
            private final String mode;
            private final IntFunction<T> constant;
            private final IntUnaryOperator fixer;
            private final List<Named<UnaryOperator<GenericTester.F<T>>>> unary = new ArrayList<>();
            private final List<Named<BinaryOperator<GenericTester.F<T>>>> binary = new ArrayList<>();

            private Builder(final String mode, final IntFunction<T> constant, final IntUnaryOperator fixer) {
                this.mode = mode;
                this.constant = constant;
                this.fixer = fixer;
            }

            public Builder<T> unary(final String name, final UnaryOperator<T> op) {
                unary.add(Named.of(name, arg -> (x, y, z) -> op.apply(arg.apply(x, y, z))));
                return this;
            }

            public Builder<T> binary(final String name, final BinaryOperator<T> op) {
                binary.add(Named.of(name, (a, b) -> (x, y, z) -> op.apply(a.apply(x, y, z), b.apply(x, y, z))));
                return this;
            }

            @Override
            public void accept(final GenericTester tester) {
                tester.modes.add(this);
            }

            private Mode build(final GenericTester tester) {
                final Set<String> ops = Stream.concat(
                        unary.stream().map(op -> op.name() + ":1"),
                        binary.stream().map(op -> op.name() + ":2")
                ).collect(Collectors.toUnmodifiableSet());
                final List<String> diff = tester.operations.stream()
                        .filter(Predicate.not(ops::contains))
                        .toList();
                Asserts.assertTrue(String.format("Missing operations for %s: %s", mode, diff), diff.isEmpty());

                final Renderer.Builder<Integer, Unit, GenericTester.F<T>> builder
                        = Renderer.builder(value -> (x, y, z) -> constant.apply(value));
                unary.forEach(op -> builder.unary(op.name(), (unit, arg) -> op.value().apply(arg)));
                binary.forEach(op -> builder.binary(op.name(), (unit, a, b) -> op.value().apply(a, b)));
                final Renderer<Integer, Unit, F<T>> renderer = builder.build();
                final TestGenerator<Integer, F<?>> genRenderer = tester.generator.build(VARS);

                return (expr, expression, full) -> {
                    final String fixed = fixer == IntUnaryOperator.identity()
                            ? expression
                            : genRenderer.render(cata(expr), NodeRenderer.FULL);
                    @SuppressWarnings("unchecked") final Expr<Integer, F<T>> converted = (Expr<Integer, F<T>>) expr;
                    final F<T> expected = renderer.render(converted, Unit.INSTANCE);
                    final IF<T> f = (x, y, z) -> expected.apply(constant.apply(x), constant.apply(y), constant.apply(z));
                    if (full) {
                        tester.testFull(mode, fixed, f);
                    } else {
                        tester.testShort(mode, fixed, f);
                    }
                };
            }

            private Expr<Integer, ? extends F<?>> cata(final Expr<Integer, ? extends F<?>> expr) {
                return expr.node(node -> node.cata(
                        c -> Node.constant(fixer.applyAsInt(c)),
                        Node::op, Node::op, Node::op
                ));
            }
        }
    }

    @FunctionalInterface
    protected interface IF<T> {
        T apply(int x, int y, int z);
    }

    @FunctionalInterface
    protected interface F<T> extends ToMiniString {
        T apply(T x, T y, T z);
    }
}

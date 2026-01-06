package expression;

import base.Asserts;
import base.Pair;
import base.TestCounter;
import expression.common.ExpressionKind;
import expression.common.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

/**
 * One-argument arithmetic expression over {@link BigDecimal}s.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
@SuppressWarnings("ClassReferencesSubclass")
public interface BigDecimalListExpression extends ToMiniString {
    BigDecimal evaluateBd(List<BigDecimal> variables);

    // Tests follow. You may temporarily remove everything til the end.

    Add EXAMPLE = new Add(
            new Subtract(new Variable(0), new Const(BigDecimal.ONE)),
            new Multiply(new Variable(1), new Const(BigDecimal.TEN))
    );

    Type<BigDecimal> TYPE = new Type<>(
            v -> new BigDecimal(v + ".000"),
            random -> BigDecimal.valueOf(random.getRandom().nextGaussian()),
            BigDecimal.class
    );
    ExpressionKind<BigDecimalListExpression, BigDecimal> KIND = new ExpressionKind<>(
            TYPE,
            BigDecimalListExpression.class,
            (r, c) -> IntStream.range(0, c)
                    .mapToObj(name -> Pair.<String, BigDecimalListExpression>of("$" + name, new Variable(name)))
                    .toList(),
            (expr, variables, values) -> expr.evaluateBd(values)
    );

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    static ExpressionTester<?, ?> tester(final TestCounter counter) {
        Asserts.assertEquals("Example toString()", "(($0 - 1) + ($1 * 10))", EXAMPLE.toString());
        Asserts.assertEquals(
                EXAMPLE + " at (2, 3)",
                BigDecimal.valueOf(31),
                EXAMPLE.evaluateBd(List.of(BigDecimal.valueOf(2), BigDecimal.valueOf(3)))
        );

        final Variable vx = new Variable(0);
        final Variable vy = new Variable(1);

        return new ExpressionTester<>(
                counter, KIND, c -> v -> c,
                (op, a, b) -> v -> op.apply(a.evaluateBd(v), b.evaluateBd(v)),
                BigDecimal::add, BigDecimal::subtract, BigDecimal::multiply, BigDecimal::divide
        )
                .basic("10", "10", v -> v(10), c(10))
                .basic("$x", "$x", BigDecimalListExpression::x, vx)
                .basic("$y", "$y", BigDecimalListExpression::y, vy)
                .basic("($x + $y)", "$x + $y", v -> x(v).add(y(v)), new Add(vx, vy))
                .basic("($x + 2)", "$x + 2", v -> x(v).add(v(2)), new Add(vx, c(2)))
                .basic("(2 - $x)", "2 - $x", v -> v(2).subtract(x(v)), new Subtract(c(2), vx))
                .basic("(3 * $x)", "3 * $x", v -> v(3).multiply(x(v)), new Multiply(c(3), vx))
                .basic("($x + $x)", "$x + $x", v -> x(v).add(x(v)), new Add(vx, vx))
                .basic("($x / -2)", "$x / -2", v -> x(v).divide(v(-2)), new Divide(vx, c(-2)))
                .basic("(2 + $x)", "2 + $x", v -> v(2).add(x(v)), new Add(c(2), vx))
                .basic("((1 + 2) + 3)", "1 + 2 + 3", v -> v(6), new Add(new Add(c(1), c(2)), c(3)))
                .basic("(1 + (2 * 3))", "1 + 2 * 3", v -> v(7), new Add(c(1), new Multiply(c(2), c(3))))
                .basic("(1 - (2 * 3))", "1 - 2 * 3", v -> v(-5), new Subtract(c(1), new Multiply(c(2), c(3))))
                .basic("(1 + (2 + 3))", "1 + 2 + 3", v -> v(6), new Add(c(1), new Add(c(2), c(3))))
                .basic("((1 - 2) - 3)", "1 - 2 - 3", v -> v(-4), new Subtract(new Subtract(c(1), c(2)), c(3)))
                .basic("(1 - (2 - 3))", "1 - (2 - 3)", v -> v(2), new Subtract(c(1), new Subtract(c(2), c(3))))
                .basic("((1 * 2) * 3)", "1 * 2 * 3", v -> v(6), new Multiply(new Multiply(c(1), c(2)), c(3)))
                .basic("(1 * (2 * 3))", "1 * 2 * 3", v -> v(6), new Multiply(c(1), new Multiply(c(2), c(3))))
                .basic("((10 / 2) / 3)", "10 / 2 / 3", v -> v(10).divide(v(2)).divide(v(3)), new Divide(new Divide(c(10), c(2)), c(3)))
                .basic("(10 / (3 / 2))", "10 / (3 / 2)", v -> v(10).divide(v(3).divide(v(2))), new Divide(c(10), new Divide(c(3), c(2))))
                .basic("(($x * $x) + (($x - 1) / 10))",
                        "$x * $x + ($x - 1) / 10",
                        v -> x(v).multiply(x(v)).add(x(v).subtract(v(1)).divide(v(10))),
                        new Add(new Multiply(vx, vx), new Divide(new Subtract(vx, c(1)), c(10)))
                )
                .basic("($x * -1000000000)", "$x * -1000000000", v -> x(v).multiply(v(-1_000_000_000)), new Multiply(vx, c(-1_000_000_000)))
                .basic("($x * -1000000000000000)", "$x * -1000000000000000", v -> x(v).multiply(v(-1_000_000_000_000_000L)), new Multiply(vx, c(-1_000_000_000_000_000L)))
                .basic("(10 / $x)", "10 / $x", v -> v(10).divide(x(v)), new Divide(c(10), vx))
                .basic("($x / $x)", "$x / $x", v -> x(v).divide(x(v)), new Divide(vx, vx))

                .advanced("(2 + 1)", "2 + 1", v -> v(2 + 1), new Add(c(2), c(1)))
                .advanced("($x - 1)", "$x - 1", v -> x(v).subtract(v(1)), new Subtract(vx, c(1)))
                .advanced("(1 * 2)", "1 * 2", v -> v(1 * 2), new Multiply(c(1), c(2)))
                .advanced("($x / 1)", "$x / 1", v -> x(v).divide(v(1)), new Divide(vx, c(1)))
                .advanced("(1 + (2 + 1))", "1 + 2 + 1", v -> v(1 + 2 + 1), new Add(c(1), new Add(c(2), c(1))))
                .advanced("($x - ($x - 1))", "$x - ($x - 1)", v -> x(v).subtract(x(v).subtract(v(1))), new Subtract(vx, new Subtract(vx, c(1))))
                .advanced("(2 * ($x / 1))", "2 * ($x / 1)", v -> v(2).multiply(x(v).divide(v(1))), new Multiply(c(2), new Divide(vx, c(1))))
                .advanced("(2 / ($x - 1))", "2 / ($x - 1)", v -> v(2).divide(x(v).subtract(v(1))), new Divide(c(2), new Subtract(vx, c(1))))
                .advanced("((1 * 2) + $x)", "1 * 2 + $x", v -> v(1 * 2).add(x(v)), new Add(new Multiply(c(1), c(2)), vx))
                .advanced("(($x - 1) - 2)", "$x - 1 - 2", v -> x(v).subtract(v(3)), new Subtract(new Subtract(vx, c(1)), c(2)))
                .advanced("(($x / 1) * 2)", "$x / 1 * 2", v -> x(v).multiply(v(2)), new Multiply(new Divide(vx, c(1)), c(2)))
                .advanced("((2 + 1) / 1)", "(2 + 1) / 1", v -> v(3), new Divide(new Add(c(2), c(1)), c(1)))
                .advanced(
                        "(1 + (1 + (2 + 1)))",
                        "1 + 1 + 2 + 1",
                        v -> v(1 + 1 + 2 + 1),
                        new Add(c(1), new Add(c(1), new Add(c(2), c(1))))
                )
                .advanced(
                        "($x - ((1 * 2) + $x))",
                        "$x - (1 * 2 + $x)",
                        v -> x(v).subtract(v(1 * 2).add(x(v))),
                        new Subtract(vx, new Add(new Multiply(c(1), c(2)), vx))
                )
                .advanced(
                        "($x * (2 / ($x - 1)))",
                        "$x * (2 / ($x - 1))",
                        v -> x(v).multiply(v(2).divide(x(v).subtract(v(1)))),
                        new Multiply(vx, new Divide(c(2), new Subtract(vx, c(1))))
                )
                .advanced(
                        "($x / (1 + (2 + 1)))",
                        "$x / (1 + 2 + 1)",
                        v -> x(v).divide(v(1 + 2 + 1)),
                        new Divide(vx, new Add(c(1), new Add(c(2), c(1))))
                )
                .advanced(
                        "((1 * 2) + (2 + 1))",
                        "1 * 2 + 2 + 1",
                        v -> v(1 * 2 + 2 + 1),
                        new Add(new Multiply(c(1), c(2)), new Add(c(2), c(1)))
                )
                .advanced(
                        "((2 + 1) - (2 + 1))",
                        "2 + 1 - (2 + 1)",
                        v -> v(2 + 1 - (2 + 1)),
                        new Subtract(new Add(c(2), c(1)), new Add(c(2), c(1)))
                )
                .advanced(
                        "(($x - 1) * ($x / 1))",
                        "($x - 1) * ($x / 1)",
                        v -> x(v).subtract(v(1)).multiply(x(v).divide(v(1))),
                        new Multiply(new Subtract(vx, c(1)), new Divide(vx, c(1)))
                )
                .advanced(
                        "(($x - 1) / (1 * 2))",
                        "($x - 1) / (1 * 2)",
                        v -> x(v).subtract(v(1)).divide(v(2)),
                        new Divide(new Subtract(vx, c(1)), new Multiply(c(1), c(2)))
                )
                .advanced(
                        "((($x - 1) - 2) + $x)",
                        "$x - 1 - 2 + $x",
                        v -> x(v).subtract(v(3)).add(x(v)),
                        new Add(new Subtract(new Subtract(vx, c(1)), c(2)), vx)
                )
                .advanced(
                        "(((1 * 2) + $x) - 1)",
                        "1 * 2 + $x - 1",
                        v -> v(1).add(x(v)),
                        new Subtract(new Add(new Multiply(c(1), c(2)), vx), c(1))
                )
                .advanced(
                        "(((2 + 1) / 1) * $x)",
                        "(2 + 1) / 1 * $x",
                        v -> v(3).multiply(x(v)),
                        new Multiply(new Divide(new Add(c(2), c(1)), c(1)), vx)
                )
                .advanced(
                        "((2 / ($x - 1)) / 2)",
                        "2 / ($x - 1) / 2",
                        v -> v(2).divide(x(v).subtract(v(1))).divide(v(2)),
                        new Divide(new Divide(c(2), new Subtract(vx, c(1))), c(2))
                );
    }

    private static BigDecimal x(final List<BigDecimal> vars) {
        return vars.get(0);
    }

    private static BigDecimal y(final List<BigDecimal> vars) {
        return vars.get(1);
    }

    private static Const c(final BigDecimal v) {
        return TYPE.constant(v);
    }

    private static Const c(final long v) {
        return TYPE.constant(v(v));
    }

    private static BigDecimal v(final long v) {
        return BigDecimal.valueOf(v);
    }

    static void main(final String... args) {
        TripleExpression.SELECTOR
                .variant("BigDecimalList", ExpressionTest.v(BigDecimalListExpression::tester))
                .main(args);
    }
}

package expression;

import base.Asserts;
import base.ExtendedRandom;
import base.Pair;
import base.TestCounter;
import expression.common.ExpressionKind;
import expression.common.Type;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("ClassReferencesSubclass")
@FunctionalInterface
public interface ListExpression extends ToMiniString {
    int evaluate(List<Integer> variables);

    // Tests follow. You may temporarily remove everything til the end.

    Add EXAMPLE = new Add(
            new Subtract(new Variable(0), new Const(1)),
            new Multiply(new Variable(1), new Const(10))
    );

    Type<Integer> TYPE = new Type<>(a -> a, ExtendedRandom::nextInt, int.class);
    ExpressionKind<ListExpression, Integer> KIND = new ExpressionKind<>(
            TYPE,
            ListExpression.class,
            (r, c) -> IntStream.range(0, c)
                    .mapToObj(name -> Pair.<String, ListExpression>of("$" + name, new Variable(name)))
                    .toList(),
            (expr, variables, values) -> expr.evaluate(values)
    );

    private static Const c(final Integer c) {
        return TYPE.constant(c);
    }

    @SuppressWarnings({"PointlessArithmeticExpression", "Convert2MethodRef"})
    static ExpressionTester<?, ?> tester(final TestCounter counter) {
        Asserts.assertEquals("Example toString()", "(($0 - 1) + ($1 * 10))", EXAMPLE.toString());
        Asserts.assertEquals(EXAMPLE + " at (2, 3)", 31, EXAMPLE.evaluate(List.of(2, 3)));

        final Variable vx = new Variable(0);
        return new ExpressionTester<>(
                counter, KIND, c -> vars -> c,
                (op, a, b) -> vars -> op.apply(a.evaluate(vars), b.evaluate(vars)),
                (a, b) -> a + b, (a, b) -> a - b, (a, b) -> a * b, (a, b) -> a / b
        )
                .basic("10", "10", vars -> 10, c(10))
                .basic("$x", "$x", ListExpression::x, vx)
                .basic("($x + 2)", "$x + 2", vars -> x(vars) + 2, new Add(vx, c(2)))
                .basic("(2 - $x)", "2 - $x", vars -> 2 - x(vars), new Subtract(c(2), vx))
                .basic("(3 * $x)", "3 * $x", vars -> 3 * x(vars), new Multiply(c(3), vx))
                .basic("($x + $x)", "$x + $x", vars -> x(vars) + x(vars), new Add(vx, vx))
                .basic("($x / -2)", "$x / -2", vars -> -x(vars) / 2, new Divide(vx, c(-2)))
                .basic("(2 + $x)", "2 + $x", vars -> 2 + x(vars), new Add(c(2), vx))
                .basic("((1 + 2) + 3)", "1 + 2 + 3", vars -> 6, new Add(new Add(c(1), c(2)), c(3)))
                .basic("(1 + (2 + 3))", "1 + 2 + 3", vars -> 6, new Add(c(1), new Add(c(2), c(3))))
                .basic("((1 - 2) - 3)", "1 - 2 - 3", vars -> -4, new Subtract(new Subtract(c(1), c(2)), c(3)))
                .basic("(1 - (2 - 3))", "1 - (2 - 3)", vars -> 2, new Subtract(c(1), new Subtract(c(2), c(3))))
                .basic("((1 * 2) * 3)", "1 * 2 * 3", vars -> 6, new Multiply(new Multiply(c(1), c(2)), c(3)))
                .basic("(1 * (2 * 3))", "1 * 2 * 3", vars -> 6, new Multiply(c(1), new Multiply(c(2), c(3))))
                .basic("((10 / 2) / 3)", "10 / 2 / 3", vars -> 10 / 2 / 3, new Divide(new Divide(c(10), c(2)), c(3)))
                .basic("(10 / (3 / 2))", "10 / (3 / 2)", vars -> 10 / (3 / 2), new Divide(c(10), new Divide(c(3), c(2))))
                .basic("(10 * (3 / 2))", "10 * (3 / 2)", vars -> 10 * (3 / 2), new Multiply(c(10), new Divide(c(3), c(2))))
                .basic("(10 + (3 - 2))", "10 + 3 - 2", vars -> 10 + (3 - 2), new Add(c(10), new Subtract(c(3), c(2))))
                .basic(
                        "(($x * $x) + (($x - 1) / 10))",
                        "$x * $x + ($x - 1) / 10",
                        vars -> x(vars) * x(vars) + (x(vars) - 1) / 10,
                        new Add(new Multiply(vx, vx), new Divide(new Subtract(vx, c(1)), c(10)))
                )
                .basic(
                        "($x * -1000000000)",
                        "$x * -1000000000",
                        vars -> x(vars) * -1_000_000_000,
                        new Multiply(vx, c(-1_000_000_000))
                )
                .basic("(10 / $x)", "10 / $x", vars -> 10 / x(vars), new Divide(c(10), vx))
                .basic("($x / $x)", "$x / $x", vars -> x(vars) / x(vars), new Divide(vx, vx))

                .advanced("(2 + 1)", "2 + 1", vars -> 2 + 1, new Add(c(2), c(1)))
                .advanced("($x - 1)", "$x - 1", vars -> x(vars) - 1, new Subtract(vx, c(1)))
                .advanced("(1 * 2)", "1 * 2", vars -> 1 * 2, new Multiply(c(1), c(2)))
                .advanced("($x / 1)", "$x / 1", vars -> x(vars) / 1, new Divide(vx, c(1)))
                .advanced("(1 + (2 + 1))", "1 + 2 + 1", vars -> 1 + 2 + 1, new Add(c(1), new Add(c(2), c(1))))
                .advanced(
                        "($x - ($x - 1))",
                        "$x - ($x - 1)",
                        vars -> x(vars) - (x(vars) - 1),
                        new Subtract(vx, new Subtract(vx, c(1)))
                )
                .advanced(
                        "(2 * ($x / 1))",
                        "2 * ($x / 1)",
                        vars -> 2 * (x(vars) / 1),
                        new Multiply(c(2), new Divide(vx, c(1)))
                )
                .advanced(
                        "(2 / ($x - 1))",
                        "2 / ($x - 1)",
                        vars -> 2 / (x(vars) - 1),
                        new Divide(c(2), new Subtract(vx, c(1)))
                )
                .advanced(
                        "((1 * 2) + $x)",
                        "1 * 2 + $x",
                        vars -> 1 * 2 + x(vars),
                        new Add(new Multiply(c(1), c(2)), vx)
                )
                .advanced(
                        "(($x - 1) - 2)",
                        "$x - 1 - 2",
                        vars -> x(vars) - 1 - 2,
                        new Subtract(new Subtract(vx, c(1)), c(2))
                )
                .advanced(
                        "(($x / 1) * 2)",
                        "$x / 1 * 2",
                        vars -> x(vars) / 1 * 2,
                        new Multiply(new Divide(vx, c(1)), c(2))
                )
                .advanced("((2 + 1) / 1)", "(2 + 1) / 1", vars -> (2 + 1) / 1, new Divide(new Add(c(2), c(1)), c(1)))
                .advanced(
                        "(1 + (1 + (2 + 1)))",
                        "1 + 1 + 2 + 1",
                        vars -> 1 + 1 + 2 + 1,
                        new Add(c(1), new Add(c(1), new Add(c(2), c(1))))
                )
                .advanced(
                        "($x - ((1 * 2) + $x))",
                        "$x - (1 * 2 + $x)",
                        vars -> x(vars) - (1 * 2 + x(vars)),
                        new Subtract(vx, new Add(new Multiply(c(1), c(2)), vx))
                )
                .advanced(
                        "($x * (2 / ($x - 1)))",
                        "$x * (2 / ($x - 1))",
                        vars -> x(vars) * (2 / (x(vars) - 1)),
                        new Multiply(vx, new Divide(c(2), new Subtract(vx, c(1))))
                )
                .advanced(
                        "($x / (1 + (2 + 1)))",
                        "$x / (1 + 2 + 1)",
                        vars -> x(vars) / (1 + 2 + 1),
                        new Divide(vx, new Add(c(1), new Add(c(2), c(1))))
                )
                .advanced(
                        "((1 * 2) + (2 + 1))",
                        "1 * 2 + 2 + 1",
                        vars -> 1 * 2 + 2 + 1,
                        new Add(new Multiply(c(1), c(2)), new Add(c(2), c(1)))
                )
                .advanced(
                        "((2 + 1) - (2 + 1))",
                        "2 + 1 - (2 + 1)",
                        vars -> 2 + 1 - (2 + 1),
                        new Subtract(new Add(c(2), c(1)), new Add(c(2), c(1)))
                )
                .advanced(
                        "(($x - 1) * ($x / 1))",
                        "($x - 1) * ($x / 1)",
                        vars -> (x(vars) - 1) * (x(vars) / 1),
                        new Multiply(new Subtract(vx, c(1)), new Divide(vx, c(1)))
                )
                .advanced(
                        "(($x - 1) / (1 * 2))",
                        "($x - 1) / (1 * 2)",
                        vars -> (x(vars) - 1) / (1 * 2),
                        new Divide(new Subtract(vx, c(1)), new Multiply(c(1), c(2)))
                )
                .advanced(
                        "((($x - 1) - 2) + $x)",
                        "$x - 1 - 2 + $x",
                        vars -> x(vars) - 1 - 2 + x(vars),
                        new Add(new Subtract(new Subtract(vx, c(1)), c(2)), vx)
                )
                .advanced(
                        "(((1 * 2) + $x) - 1)",
                        "1 * 2 + $x - 1",
                        vars -> 1 * 2 + x(vars) - 1,
                        new Subtract(new Add(new Multiply(c(1), c(2)), vx), c(1))
                )
                .advanced(
                        "(((2 + 1) / 1) * $x)",
                        "(2 + 1) / 1 * $x",
                        vars -> (2 + 1) / 1 * x(vars),
                        new Multiply(new Divide(new Add(c(2), c(1)), c(1)), vx)
                )
                .advanced(
                        "((2 / ($x - 1)) / 2)",
                        "2 / ($x - 1) / 2",
                        vars -> 2 / (x(vars) - 1) / 2,
                        new Divide(new Divide(c(2), new Subtract(vx, c(1))), c(2))
                );
    }

    private static Integer x(final List<Integer> vars) {
        return vars.get(0);
    }

    static void main(final String... args) {
        TripleExpression.SELECTOR
                .variant("List", ExpressionTest.v(ListExpression::tester))
                .main(args);
    }
}

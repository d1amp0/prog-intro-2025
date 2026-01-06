package expression;

import base.Asserts;
import base.Pair;
import base.TestCounter;
import expression.common.ExpressionKind;
import expression.common.Type;

import java.util.List;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
@SuppressWarnings("ClassReferencesSubclass")
public interface DoubleTripleExpression extends ToMiniString {
    double evaluateD(double x, double y, double z);

    // Tests follow. You may temporarily remove everything til the end.

    Add EXAMPLE = new Add(
            new Subtract(new Variable("x"), new Const(1.1)),
            new Multiply(new Variable("y"), new Const(10.1))
    );

    Type<Double> TYPE = new Type<>(Double::valueOf, random -> random.getRandom().nextGaussian() * 1000, double.class);
    ExpressionKind<DoubleTripleExpression, Double> KIND = new ExpressionKind<>(
            TYPE,
            DoubleTripleExpression.class,
            List.of(
                    Pair.of("x", new Variable("x")),
                    Pair.of("y", new Variable("y")),
                    Pair.of("z", new Variable("z"))
            ),
            (expr, variables, values) -> expr.evaluateD(values.get(0), values.get(1), values.get(2))
    );

    @SuppressWarnings({"PointlessArithmeticExpression", "Convert2MethodRef"})
    static ExpressionTester<?, ?> tester(final TestCounter counter) {
        Asserts.assertEquals("Example toString()", "((x - 1.1) + (y * 10.1))", EXAMPLE.toString());
//        Asserts.assertEquals(EXAMPLE + " at (2.1, 3.1)", 32.31, EXAMPLE.evaluateD(Map.of("x", 2.1, "y", 3.1)), 1e-6);

        final ConstWrapper one = w(1.1);
        final ConstWrapper two = w(2.2);
        final ConstWrapper three = w(3.3);
        final ConstWrapper ten = w(10.1);

        return new ExpressionTester<>(
                counter, KIND, c -> (x, y, z) -> c,
                (op, a, b) -> (x, y, z) -> op.apply(a.evaluateD(x, y, z), b.evaluateD(x, y, z)),
                (a, b) -> a + b, (a, b) -> a - b, (a, b) -> a * b, (a, b) -> a / b,
                Map.of("@one", one.v, "@two", two.v, "@three", three.v, "@ten", ten.v)
        )
                .basicF("@ten", "@ten", (x, y, z) -> ten.v, v -> ten.c)
                .basicF("$x", "$x", (x, y, z) -> x, DoubleTripleExpression::vx)
                .basicF("$y", "$y", (x, y, z) -> y, DoubleTripleExpression::vy)
                .basicF("$z", "$z", (x, y, z) -> z, DoubleTripleExpression::vz)
                .basicF("($x + $y)", "$x + $y", (x, y, z) -> x + y, v -> new Add(vx(v), vy(v)))
                .basicF("($z + @two)", "$z + @two", (x, y, z) -> z + two.v, v -> new Add(vz(v), two.c))
                .basicF("(@two - $x)", "@two - $x", (x, y, z) -> two.v - x, v -> new Subtract(two.c, vx(v)))
                .basicF("(@three * $x)", "@three * $x", (x, y, z) -> three.v * x, v -> new Multiply(three.c, vx(v)))
                .basicF("($x + $x)", "$x + $x", (x, y, z) -> x + x, v -> new Add(vx(v), vx(v)))
                .basicF("($x / -@two)", "$x / -@two", (x, y, z) -> -x / two.v, v -> new Divide(vx(v), c(-two.v)))
                .basicF("(@two + $x)", "@two + $x", (x, y, z) -> two.v + x, v -> new Add(two.c, vx(v)))
                .basicF(
                        "((@one + @two) + @three)",
                        "@one + @two + @three",
                        (x, y, z) -> one.v + two.v + three.v,
                        v -> new Add(new Add(one.c, two.c), three.c)
                )
                .basicF(
                        "(@one + (@two * @three))",
                        "@one + @two * @three",
                        (x, y, z) -> one.v + two.v * three.v,
                        v -> new Add(one.c, new Multiply(two.c, three.c))
                )
                .basicF(
                        "(@one - (@ten * @three))",
                        "@one - @ten * @three",
                        (x, y, z) -> one.v - ten.v * three.v,
                        v -> new Subtract(one.c, new Multiply(ten.c, three.c))
                )
                .basicF(
                        "(@one + (@two + @three))",
                        "@one + @two + @three",
                        (x, y, z) -> one.v + two.v + three.v,
                        v -> new Add(one.c, new Add(two.c, three.c))
                )
                .basicF(
                        "((@one - @two) - @three)",
                        "@one - @two - @three",
                        (x, y, z) -> one.v - two.v - three.v,
                        v -> new Subtract(new Subtract(one.c, two.c), three.c)
                )
                .basicF(
                        "(@one - (@two - @three))",
                        "@one - (@two - @three)",
                        (x, y, z) -> one.v - (two.v - three.v),
                        v -> new Subtract(one.c, new Subtract(two.c, three.c))
                )
                .basicF(
                        "((@one * @two) * @three)",
                        "@one * @two * @three",
                        (x, y, z) -> one.v * two.v * three.v,
                        v -> new Multiply(new Multiply(one.c, two.c), three.c)
                )
                .basicF(
                        "(@one * (@two * @three))",
                        "@one * @two * @three",
                        (x, y, z) -> one.v * two.v * three.v,
                        v -> new Multiply(one.c, new Multiply(two.c, three.c))
                )
                .basicF(
                        "((@ten / @two) / @three)",
                        "@ten / @two / @three",
                        (x, y, z) -> ten.v / two.v / three.v,
                        v -> new Divide(new Divide(ten.c, two.c), three.c)
                )
                .basicF(
                        "(@ten / (@three / @two))",
                        "@ten / (@three / @two)",
                        (x, y, z) -> ten.v / (three.v / two.v),
                        v -> new Divide(ten.c, new Divide(three.c, two.c))
                )
                .basicF(
                        "(@ten * (@three / @two))",
                        "@ten * (@three / @two)",
                        (x, y, z) -> ten.v * (three.v / two.v),
                        v -> new Multiply(ten.c, new Divide(three.c, two.c))
                )
                .basicF(
                        "(@ten + (@three - @two))",
                        "@ten + @three - @two",
                        (x, y, z) -> ten.v + (three.v - two.v),
                        v -> new Add(ten.c, new Subtract(three.c, two.c))
                )
                .basicF(
                        "(($x * $x) + (($x - @one) / @ten))",
                        "$x * $x + ($x - @one) / @ten",
                        (x, y, z) -> x * x + (x - one.v) / ten.v,
                        v -> new Add(new Multiply(vx(v), vx(v)), new Divide(new Subtract(vx(v), one.c), ten.c))
                )
                .basicF("($x * -1000000.0)", "$x * -1000000.0", (x, y, z) -> x * v(-1e6), v -> new Multiply(vx(v), c(-1e6)))
                .basicF("($x * -1.0E12)", "$x * -1.0E12", (x, y, z) -> x * v(-1e12), v -> new Multiply(vx(v), c(v(-1e12))))
                .basicF("(@ten / $x)", "@ten / $x", (x, y, z) -> ten.v / x, v -> new Divide(ten.c, vx(v)))
                .basicF("($x / $x)", "$x / $x", (x, y, z) -> x / x, v -> new Divide(vx(v), vx(v)))

                .advancedF("($x - $x)", "$x - $x", (x, y, z) -> x - x, v -> new Subtract(vx(v), vx(v)))
                .advancedF("(@one * $x)", "@one * $x", (x, y, z) -> one.v * x, v -> new Multiply(one.c, vx(v)))
                .advancedF("(@one / @two)", "@one / @two", (x, y, z) -> one.v / two.v, v -> new Divide(one.c, two.c))
                .advancedF("(@two + @one)", "@two + @one", (x, y, z) -> two.v + one.v, v -> new Add(two.c, one.c))
                .advancedF("($x - @one)", "$x - @one", (x, y, z) -> x - one.v, v -> new Subtract(vx(v), one.c))
                .advancedF("(@one * @two)", "@one * @two", (x, y, z) -> one.v * two.v, v -> new Multiply(one.c, two.c))
                .advancedF("($x / @one)", "$x / @one", (x, y, z) -> x / one.v, v -> new Divide(vx(v), one.c))
                .advancedF(
                        "(@one + (@two + @one))",
                        "@one + @two + @one",
                        (x, y, z) -> one.v + two.v + one.v,
                        v -> new Add(one.c, new Add(two.c, one.c))
                )
                .advancedF(
                        "($x - ($x - @one))",
                        "$x - ($x - @one)",
                        (x, y, z) -> x - (x - one.v),
                        v -> new Subtract(vx(v), new Subtract(vx(v), one.c))
                )
                .advancedF(
                        "(@two * ($x / @one))",
                        "@two * ($x / @one)",
                        (x, y, z) -> two.v * (x / one.v),
                        v -> new Multiply(two.c, new Divide(vx(v), one.c))
                )
                .advancedF(
                        "(@two / ($x - @one))",
                        "@two / ($x - @one)",
                        (x, y, z) -> two.v / (x - one.v),
                        v -> new Divide(two.c, new Subtract(vx(v), one.c))
                )
                .advancedF(
                        "((@one * @two) + $x)",
                        "@one * @two + $x",
                        (x, y, z) -> one.v * two.v + x,
                        v -> new Add(new Multiply(one.c, two.c), vx(v))
                )
                .advancedF(
                        "(($x - @one) - @two)",
                        "$x - @one - @two",
                        (x, y, z) -> x - one.v - two.v,
                        v -> new Subtract(new Subtract(vx(v), one.c), two.c)
                )
                .advancedF(
                        "(($x / @one) * @two)",
                        "$x / @one * @two",
                        (x, y, z) -> x / one.v * two.v,
                        v -> new Multiply(new Divide(vx(v), one.c), two.c)
                )
                .advancedF(
                        "((@two + @one) / @one)",
                        "(@two + @one) / @one",
                        (x, y, z) -> (two.v + one.v) / one.v,
                        v -> new Divide(new Add(two.c, one.c), one.c)
                )
                .advancedF(
                        "(@one + (@one + (@two + @one)))",
                        "@one + @one + @two + @one",
                        (x, y, z) -> one.v + one.v + two.v + one.v,
                        v -> new Add(one.c, new Add(one.c, new Add(two.c, one.c)))
                )
                .advancedF(
                        "($x - ((@one * @two) + $x))",
                        "$x - (@one * @two + $x)",
                        (x, y, z) -> x - (one.v * two.v + x),
                        v -> new Subtract(vx(v), new Add(new Multiply(one.c, two.c), vx(v)))
                )
                .advancedF(
                        "($x * (@two / ($x - @one)))",
                        "$x * (@two / ($x - @one))",
                        (x, y, z) -> x * (two.v / (x - one.v)),
                        v -> new Multiply(vx(v), new Divide(two.c, new Subtract(vx(v), one.c)))
                )
                .advancedF(
                        "($x / (@one + (@two + @one)))",
                        "$x / (@one + @two + @one)",
                        (x, y, z) -> x / (one.v + two.v + one.v),
                        v -> new Divide(vx(v), new Add(one.c, new Add(two.c, one.c)))
                )
                .advancedF(
                        "((@one * @two) + (@two + @one))",
                        "@one * @two + @two + @one",
                        (x, y, z) -> one.v * two.v + two.v + one.v,
                        v -> new Add(new Multiply(one.c, two.c), new Add(two.c, one.c))
                )
                .advancedF(
                        "((@two + @one) - (@two + @one))",
                        "@two + @one - (@two + @one)",
                        (x, y, z) -> two.v + one.v - (two.v + one.v),
                        v -> new Subtract(new Add(two.c, one.c), new Add(two.c, one.c))
                )
                .advancedF(
                        "(($x - @one) * ($x / @one))",
                        "($x - @one) * ($x / @one)",
                        (x, y, z) -> (x - one.v) * (x / one.v),
                        v -> new Multiply(new Subtract(vx(v), one.c), new Divide(vx(v), one.c))
                )
                .advancedF(
                        "(($x - @one) / (@one * @two))",
                        "($x - @one) / (@one * @two)",
                        (x, y, z) -> (x - one.v) / (one.v * two.v),
                        v -> new Divide(new Subtract(vx(v), one.c), new Multiply(one.c, two.c))
                )
                .advancedF(
                        "((($x - @one) - @two) + $x)",
                        "$x - @one - @two + $x",
                        (x, y, z) -> x - one.v - two.v + x,
                        v -> new Add(new Subtract(new Subtract(vx(v), one.c), two.c), vx(v))
                )
                .advancedF(
                        "(((@one * @two) + $x) - @one)",
                        "@one * @two + $x - @one",
                        (x, y, z) -> one.v * two.v + x - one.v,
                        v -> new Subtract(new Add(new Multiply(one.c, two.c), vx(v)), one.c)
                )
                .advancedF(
                        "(((@two + @one) / @one) * $x)",
                        "(@two + @one) / @one * $x",
                        (x, y, z) -> (two.v + one.v) / one.v * x,
                        v -> new Multiply(new Divide(new Add(two.c, one.c), one.c), vx(v))
                )
                .advancedF(
                        "($x / ($x - $x))",
                        "$x / ($x - $x)",
                        (x, y, z) -> x / (x - x),
                        v -> new Divide(vx(v), new Subtract(vx(v), vx(v)))
                )
                .advancedF(
                        "(($x - $x) + @one)",
                        "$x - $x + @one",
                        (x, y, z) -> x - x + one.v,
                        v -> new Add(new Subtract(vx(v), vx(v)), one.c)
                )
                .advancedF(
                        "(($x - $x) / @two)",
                        "($x - $x) / @two",
                        (x, y, z) -> (x - x) / two.v,
                        v -> new Divide(new Subtract(vx(v), vx(v)), two.c)
                )
                .advancedF(
                        "(@two - (@two - (@one * $x)))",
                        "@two - (@two - @one * $x)",
                        (x, y, z) -> two.v - (two.v - one.v * x),
                        v -> new Subtract(two.c, new Subtract(two.c, new Multiply(one.c, vx(v))))
                )
                .advancedF(
                        "(@one * (($x - $x) + @one))",
                        "@one * ($x - $x + @one)",
                        (x, y, z) -> one.v * (x - x + one.v),
                        v -> new Multiply(one.c, new Add(new Subtract(vx(v), vx(v)), one.c))
                )
                .advancedF(
                        "($x / (@two - (@one * $x)))",
                        "$x / (@two - @one * $x)",
                        (x, y, z) -> x / (two.v - one.v * x),
                        v -> new Divide(vx(v), new Subtract(two.c, new Multiply(one.c, vx(v))))
                )
                .advancedF(
                        "((@one * $x) + (@one / @two))",
                        "@one * $x + @one / @two",
                        (x, y, z) -> one.v * x + one.v / two.v,
                        v -> new Add(new Multiply(one.c, vx(v)), new Divide(one.c, two.c))
                )
                .advancedF(
                        "(($x + $x) - (@one * $x))",
                        "$x + $x - @one * $x",
                        (x, y, z) -> x + x - one.v * x,
                        v -> new Subtract(new Add(vx(v), vx(v)), new Multiply(one.c, vx(v)))
                )
                .advancedF(
                        "((@one * $x) * (@one / @two))",
                        "@one * $x * (@one / @two)",
                        (x, y, z) -> one.v * x * (one.v / two.v),
                        v -> new Multiply(new Multiply(one.c, vx(v)), new Divide(one.c, two.c))
                )
                .advancedF(
                        "((@one * $x) / ($x + $x))",
                        "@one * $x / ($x + $x)",
                        (x, y, z) -> one.v * x / (x + x),
                        v -> new Divide(new Multiply(one.c, vx(v)), new Add(vx(v), vx(v)))
                )
                .advancedF(
                        "((($x - $x) / @two) + @two)",
                        "($x - $x) / @two + @two",
                        (x, y, z) -> (x - x) / two.v + two.v,
                        v -> new Add(new Divide(new Subtract(vx(v), vx(v)), two.c), two.c)
                )
                .advancedF(
                        "(($x / ($x - $x)) - @one)",
                        "$x / ($x - $x) - @one",
                        (x, y, z) -> x / (x - x) - one.v,
                        v -> new Subtract(new Divide(vx(v), new Subtract(vx(v), vx(v))), one.c)
                )
                .advancedF(
                        "((@two - (@one * $x)) * @one)",
                        "(@two - @one * $x) * @one",
                        (x, y, z) -> (two.v - one.v * x) * one.v,
                        v -> new Multiply(new Subtract(two.c, new Multiply(one.c, vx(v))), one.c)
                )
                .advancedF(
                        "(($x / ($x - $x)) / $x)",
                        "$x / ($x - $x) / $x",
                        (x, y, z) -> x / (x - x) / x,
                        v -> new Divide(new Divide(vx(v), new Subtract(vx(v), vx(v))), vx(v))
                )
                .advancedF(
                        "((1.1E10 * $x) * (1.1E10 / 2.3E12))",
                        "1.1E10 * $x * (1.1E10 / 2.3E12)",
                        (x, y, z) -> v(1.1E10) * x * (v(1.1E10) / v(2.3E12)),
                        v -> new Multiply(new Multiply(c(1.1E10), vx(v)), new Divide(c(1.1E10), c(2.3E12)))
                )
                .advancedF(
                        "((1.1E10 * $x) / ($x + $x))",
                        "1.1E10 * $x / ($x + $x)",
                        (x, y, z) -> v(1.1E10) * x / (x + x),
                        v -> new Divide(new Multiply(c(1.1E10), vx(v)), new Add(vx(v), vx(v)))
                )
                .advancedF(
                        "((($x - $x) / 2.3E12) + 2.3E12)",
                        "($x - $x) / 2.3E12 + 2.3E12",
                        (x, y, z) -> (x - x) / v(2.3E12) + v(2.3E12),
                        v -> new Add(new Divide(new Subtract(vx(v), vx(v)), c(2.3E12)), c(2.3E12))
                )
                .advancedF(
                        "(($x / ($x - $x)) - 1.1E10)",
                        "$x / ($x - $x) - 1.1E10",
                        (x, y, z) -> x / (x - x) - v(1.1E10),
                        v -> new Subtract(new Divide(vx(v), new Subtract(vx(v), vx(v))), c(1.1E10))
                )
                .advancedF(
                        "((2.3E12 - (1.1E10 * $x)) * 1.1E10)",
                        "(2.3E12 - 1.1E10 * $x) * 1.1E10",
                        (x, y, z) -> (v(2.3E12) - v(1.1E10) * x) * v(1.1E10),
                        v -> new Multiply(new Subtract(c(2.3E12), new Multiply(c(1.1E10), vx(v))), c(1.1E10))
                )
                .advancedF(
                        "((@two / ($x - @one)) / @two)",
                        "@two / ($x - @one) / @two",
                        (x, y, z) -> two.v / (x - one.v) / two.v,
                        v -> new Divide(new Divide(two.c, new Subtract(vx(v), one.c)), two.c)
                );
    }

    private static Variable vx(final List<String> vars) {
        return new Variable(vars.get(0));
    }

    private static Variable vy(final List<String> vars) {
        return new Variable(vars.get(1));
    }

    private static Variable vz(final List<String> vars) {
        return new Variable(vars.get(2));
    }

    private static Const c(final double v) {
        return TYPE.constant(v(v));
    }

    private static double v(final double v) {
        return v;
    }

    private static ConstWrapper w(final double v) {
        return new ConstWrapper(v);
    }

    class ConstWrapper {
        private final Const c;
        private final double v;

        public ConstWrapper(final double v) {
            this.c = c(v);
            this.v = v;
        }
    }

    static void main(final String... args) {
        TripleExpression.SELECTOR
                .variant("DoubleTriple", ExpressionTest.v(DoubleTripleExpression::tester))
                .main(args);
    }
}

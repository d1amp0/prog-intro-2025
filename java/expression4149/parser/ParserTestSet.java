package expression.parser;

import base.*;
import expression.ToMiniString;
import expression.common.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ParserTestSet<E extends ToMiniString, C> {
    private static final int D = 5;

    private static final List<Integer> TEST_VALUES = new ArrayList<>();
    static {
        Functional.addRange(TEST_VALUES, D, D);
        Functional.addRange(TEST_VALUES, D, -D);
    }

    public static final List<Integer> CONSTS
            = List.of(0, 1, -1, 4, -4, 10, -10, 30, -30, 100, -100, Integer.MAX_VALUE, Integer.MIN_VALUE);

    protected final ParserTester tester;
    protected final ParsedKind<E, C> kind;
    private final boolean safe;

    protected final TestCounter counter;

    public ParserTestSet(final ParserTester tester, final ParsedKind<E, C> kind) {
        this(tester, kind, true);
    }

    protected ParserTestSet(final ParserTester tester, final ParsedKind<E, C> kind, final boolean safe) {
        this.tester = tester;
        this.kind = kind;
        this.safe = safe;

        counter = tester.getCounter();
    }

    private void examples(final TestGenerator<Integer, E> generator) {
        example(generator, "$x+2", (x, y, z) -> x + 2);
        example(generator, "2-$y", (x, y, z) -> 2 - y);
        example(generator, "  3*  $z  ", (x, y, z) -> 3 * z);
        example(generator, "$x/  -  2", (x, y, z) -> -x / 2);
        example(generator, "$x*$y+($z-1   )/10", (x, y, z) -> x * y + (int) (z - 1) / 10);
        example(generator, "-(-(-\t\t-5 + 16   *$x*$y) + 1 * $z) -(((-11)))", (x, y, z) -> -(-(5 + 16 * x * y) + z) + 11);
        example(generator, "" + Integer.MAX_VALUE, (x, y, z) -> (long) Integer.MAX_VALUE);
        example(generator, "" + Integer.MIN_VALUE, (x, y, z) -> (long) Integer.MIN_VALUE);
        example(generator, "$x--$y--$z", (x, y, z) -> x + y + z);
        example(generator, "((2+2))-0/(--2)*555", (x, y, z) -> 4L);
        example(generator, "$x-$x+$y-$y+$z-($z)", (x, y, z) -> 0L);
        example(generator, "(".repeat(300) + "$x + $y + (-10*-$z)" + ")".repeat(300), (x, y, z) -> x + y + 10 * z);
        example(generator, "$x / $y / $z", (x, y, z) -> y == 0 || z == 0 ? Reason.DBZ.error() : (int) x / (int) y / z);
    }

    private void example(final TestGenerator<Integer, E> generator, final String name, final ExampleExpression expression) {
        final List<Pair<String, E>> variables = generator.variables(3);
        final List<String> names = Functional.map(variables, Pair::first);
        final String mangled = name
                .replace("$x", names.get(0))
                .replace("$y", names.get(1))
                .replace("$z", names.get(2));
        final TExpression expected = vars -> expression.evaluate(vars.get(0), vars.get(1), vars.get(2));

        counter.test(() -> {
            final E parsed = parse(mangled, names, true);
            Functional.allValues(TEST_VALUES, 3).forEach(values -> check(expected, parsed, names, values, mangled));
        });
    }

    protected void test() {
        final TestGenerator<Integer, E> generator = tester.generator.build(kind.kind.variables());
        final Renderer<Integer, Unit, TExpression> renderer = tester.renderer.build();
        final Consumer<TestGenerator.Test<Integer, E>> consumer = test -> test(renderer, test);
        counter.scope("Basic tests", () -> generator.testBasic(consumer));
        counter.scope("Handmade tests", () -> examples(generator));
        counter.scope("Random tests", () -> generator.testRandom(counter, 1, consumer));
    }

    private void test(final Renderer<Integer, Unit, TExpression> renderer, final TestGenerator.Test<Integer, E> test) {
        final Expr<Integer, E> expr = test.expr;
        final List<Pair<String, E>> vars = expr.variables();
        final List<String> variables = Functional.map(vars, Pair::first);
        final String full = test.render(NodeRenderer.FULL);
        final String mini = test.render(NodeRenderer.MINI);

        final E fullParsed = parse(test, variables, NodeRenderer.FULL);
        final E miniParsed = parse(test, variables, NodeRenderer.MINI);
        final E safeParsed = parse(test, variables, NodeRenderer.SAME);

        checkToString(full, mini, "base", fullParsed);
        if (tester.mode() > 0) {
            counter.test(() -> Asserts.assertEquals("mini.toMiniString", mini, miniParsed.toMiniString()));
            counter.test(() -> Asserts.assertEquals("safe.toMiniString", mini, safeParsed.toMiniString()));
        }
        checkToString(full, mini, "extraParentheses", parse(test, variables, NodeRenderer.FULL_EXTRA));
        checkToString(full, mini, "noSpaces", parse(removeSpaces(full), variables, false));
        checkToString(full, mini, "extraSpaces", parse(extraSpaces(full), variables, false));

        final TExpression expected = renderer.render(
                Expr.of(
                        expr.node(),
                        Functional.map(vars, (i, var) -> Pair.of(var.first(), args -> args.get(i)))
                ),
                Unit.INSTANCE
        );

        check(expected, fullParsed, variables, tester.random().random(variables.size(), ExtendedRandom::nextInt), full);
        if (this.safe) {
            final String safe = test.render(NodeRenderer.SAME);
            check(expected, safeParsed, variables, tester.random().random(variables.size(), ExtendedRandom::nextInt), safe);
        }
    }

    private E parse(
            final TestGenerator.Test<Integer, E> test,
            final List<String> variables,
            final NodeRenderer.Settings settings
    ) {
        return parse(test.render(settings.withParens(tester.parens)), variables, false);
    }

    private static final String LOOKBEHIND = "(?<![a-zA-Z0-9<>*/+=!-])";
    private static final String LOOKAHEAD = "(?![a-zA-Z0-9<>*/])";
    private static final Pattern SPACES = Pattern.compile(LOOKBEHIND + " | " + LOOKAHEAD + "|" + LOOKAHEAD + LOOKBEHIND);
    private String extraSpaces(final String expression) {
        return SPACES.matcher(expression).replaceAll(r -> tester.random().randomString(
                ExtendedRandom.SPACES,
                tester.random().nextInt(5)
        ));
    }

    private static String removeSpaces(final String expression) {
        return SPACES.matcher(expression).replaceAll("");
    }

    private void checkToString(final String full, final String mini, final String context, final ToMiniString parsed) {
        counter.test(() -> {
            assertEquals(context + ".toString", full, full, parsed.toString());
            if (tester.mode() > 0) {
                assertEquals(context + ".toMiniString", full, mini, parsed.toMiniString());
            }
        });
    }

    private static void assertEquals(
            final String context,
            final String original,
            final String expected,
            final String actual
    ) {
        final String message = String.format("%s:%n     original `%s`,%n     expected `%s`,%n       actual `%s`",
                context, original, expected, actual);
        Asserts.assertTrue(message, Objects.equals(expected, actual));
    }

    private Either<Reason, Integer> eval(final TExpression expression, final List<Integer> vars) {
        return Reason.eval(() -> tester.cast(expression.evaluate(vars)));
    }

    protected E parse(final String expression, final List<String> variables, final boolean reparse) {
        return counter.testV(() -> {
            final E parsed = counter.testV(() -> counter.call("parse",
                    () -> kind.parse(expression, variables)));
            if (reparse) {
                counter.testV(() -> counter.call("parse", () -> kind.parse(parsed.toString(), variables)));
            }
            return parsed;
        });
    }

    private void check(
            final TExpression expectedExpression,
            final E expression,
            final List<String> variables,
            final List<Integer> values,
            final String unparsed
    ) {
        counter.test(() -> {
            final Either<Reason, Integer> answer = eval(expectedExpression, values);
            final String args = IntStream.range(0, variables.size())
                    .mapToObj(i -> variables.get(i) + "=" + values.get(i))
                    .collect(Collectors.joining(", "));
            final String message = String.format("f(%s)%n\twhere f=%s%n\tyour f=%s", args, unparsed, expression);
            try {
                final C actual = kind.kind.evaluate(expression, variables, kind.kind.fromInts(values));
                counter.checkTrue(answer.isRight(), "Error expected for f(%s)%n\twhere f=%s%n\tyour f=%s", args, unparsed, expression);
                Asserts.assertEquals(message, answer.getRight(), actual);
            } catch (final Exception e) {
                if (answer.isRight()) {
                    counter.fail(e, "No error expected for %s", message);
                }
            }
        });
    }

    @FunctionalInterface
    public interface TExpression {
        long evaluate(List<Integer> vars);
    }

    @FunctionalInterface
    protected interface ExampleExpression {
        long evaluate(long x, long y, long z);
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    public static class ParsedKind<E extends ToMiniString, C> {
        private final ExpressionKind<E, C> kind;
        private final Parser<E> parser;

        public ParsedKind(final ExpressionKind<E, C> kind, final Parser<E> parser) {
            this.kind = kind;
            this.parser = parser;
        }

        public E parse(final String expression, final List<String> variables) throws Exception {
            return parser.parse(expression, variables);
        }

        @Override
        public String toString() {
            return kind.toString();
        }
    }

    @FunctionalInterface
    public interface Parser<E> {
        E parse(final String expression, final List<String> variables) throws Exception;
    }
}

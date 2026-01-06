package expression.parser;

import base.ExtendedRandom;
import base.Pair;
import base.Selector;
import expression.TripleExpression;
import expression.Variable;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static expression.parser.Operations.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final TripleParser PARSER = new ExpressionParser();
    private static final Consumer<ParserTester> TRIPLE = kind(
            TripleExpression.KIND,
            (expr, variables) -> PARSER.parse(expr)
    );

    private static final String LETTERS = ExtendedRandom.ENGLISH.substring(0, 23) + ExtendedRandom.ENGLISH.toUpperCase().substring(1, 23);
    private static Operations.Operation triple(final String pattern) {
        return kind(
                TripleExpression.KIND.withVariables((random, count) -> Stream.of("x", "y", "z")
                        .map(name -> pattern.replace("n", name).replace("r", random.randomString(LETTERS)))
                        .map(name -> Pair.<String, TripleExpression>of(name, new Variable(name)))
                        .toList()
                ),
                (expr, variables) -> PARSER.parse(expr)
        );
    }

    // === Common

    public static final Selector SELECTOR = Selector.composite(ParserTest.class, ParserTester::new, "easy", "hard")
            .variant("Base", TRIPLE, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("4142", AND, XOR, OR)
            .variant("4749", SHIFT_L, SHIFT_R, SHIFT_A)

            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

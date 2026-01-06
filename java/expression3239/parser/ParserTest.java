package expression.parser;

import base.Selector;
import expression.ListExpression;

import static expression.parser.Operations.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ParserTest {
    private static final ExpressionParser PARSER = new ExpressionParser();
    private static final Operations.Operation LIST = kind(ListExpression.KIND, PARSER::parse);

    // === Common

    public static final Selector SELECTOR = Selector.composite(ParserTest.class, ParserTester::new, "easy", "hard")
            .variant("Base", LIST, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("3637", MIN, MAX, REVERSE)
            .variant("3839", MIN, MAX, REVERSE, DIGITS)
            .variant("3435", FLOOR, CEILING, SET, CLEAR)
            .variant("3233", FLOOR, CEILING)
            .selector();

    private ParserTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

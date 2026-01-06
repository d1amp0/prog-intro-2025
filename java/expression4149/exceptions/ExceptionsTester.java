package expression.exceptions;

import base.Named;
import base.TestCounter;
import expression.common.Reason;
import expression.parser.ParserTestSet;
import expression.parser.ParserTester;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongToIntFunction;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ExceptionsTester extends ParserTester {
    /* package-private */ final List<Named<String>> parsingTest = new ArrayList<>(List.of(
            Named.of("No first argument", "* y * z"),
            Named.of("No middle argument", "x *  * z"),
            Named.of("No last argument", "x * y * "),
            Named.of("No first argument'", "1 + (* y * z) + 2"),
            Named.of("No middle argument'", "1 + (x *  / 9) + 3"),
            Named.of("No last argument'", "1 + (x * y - ) + 3"),
            Named.of("No opening parenthesis", "x * y)"),
            Named.of("No closing parenthesis", "(x * y"),
            Named.of("Mismatched closing parenthesis", "(x * y]"),
            Named.of("Mismatched open parenthesis", "[x * y)"),
            Named.of("Start symbol", "@x * y"),
            Named.of("Middle symbol", "x @ * y"),
            Named.of("End symbol", "x * y@"),
            Named.of("Constant overflow 1", Integer.MIN_VALUE - 1L + ""),
            Named.of("Constant overflow 2", Integer.MAX_VALUE + 1L + ""),
            Named.of("Bare +", "+"),
            Named.of("Bare -", "-"),
            Named.of("Bare a", "a"),
            Named.of("(())", "(())"),
            Named.of("Spaces in numbers", "10 20")
    ));

    public ExceptionsTester(final TestCounter counter) {
        super(counter);
    }


    private void parsingTests(final String... tests) {
        for (final String test : tests) {
            parsingTest.add(Named.of(test, test));
        }
    }

    @Override
    public void unary(final String name, final int priority, final BiFunction<Long, LongToIntFunction, Long> op) {
        if (allowed(name)) {
            parsingTests(name, "1 * " + name, name + " * 1");
        }
        parsingTests(name + "()", name + "(1, 2)");
        if (name.length() > 1) {
            parsingTests(name + "q");
        }
        if (allLetterAndDigit(name)) {
            parsingTests(name + "1", name + "q");
        }
        super.unary(name, priority, op);
    }

    private static boolean allowed(final String name) {
        return !"xyz".contains(name.substring(0, 1)) && !"xyz".contains(name.substring(name.length() - 1));
    }

    @Override
    public void binary(final String name, final int priority, final LongBinaryOperator op) {
        if (allowed(name)) {
            parsingTests(name);
        }
        parsingTests("1 " + name, "1 " + name + " * 3");
        if (!"-".equals(name)) {
            parsingTests(name + " 1", "1 * " + name + " 2");
        }
        if (allLetterAndDigit(name)) {
            parsingTests("5" + name + "5", "5 " + name + "5", "5 " + name + "5 5", "1" + name + "x 1", "1 " + name + "x 1");
        }
        super.binary(name, priority, op);
    }

    private static boolean allLetterAndDigit(final String name) {
        return name.chars().allMatch(Character::isLetterOrDigit);
    }

    @Override
    protected void test(final ParserTestSet.ParsedKind<?, ?> kind) {
        new ExceptionsTestSet<>(this, kind).test();
    }

    @Override
    protected int cast(final long value) {
        return Reason.overflow(value);
    }
}

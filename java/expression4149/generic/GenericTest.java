package expression.generic;

import base.Selector;

import java.math.BigInteger;
import java.util.function.*;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class GenericTest {
    // === Base
    private static final Consumer<GenericTester> ADD = binary("+", 200);
    private static final Consumer<GenericTester> SUBTRACT = binary("-", -200);
    private static final Consumer<GenericTester> MULTIPLY = binary("*", 301);
    private static final Consumer<GenericTester> DIVIDE = binary("/", -300);
    private static final Consumer<GenericTester> NEGATE = unary("-");

    // === Cmm
    private static final Consumer<GenericTester> COUNT = unary("count");
    private static final Consumer<GenericTester> MIN = binary("min", 50);
    private static final Consumer<GenericTester> MAX = binary("max", 50);

    // === Checked integers
    private static Integer i(final long v) {
        if (v != (int) v) {
            throw new ArithmeticException("Overflow");
        }
        return (int) v;
    }

    private static final GenericTester.Mode.Builder<Integer> INTEGER_CHECKED = mode("i", c -> c)
            .binary("+", (a, b) -> i(a + (long) b))
            .binary("-", (a, b) -> i(a - (long) b))
            .binary("*", (a, b) -> i(a * (long) b))
            .binary("/", (a, b) -> i(a / (long) b))
            .unary("-", a -> i(- (long) a))

            .unary("count", Integer::bitCount)
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;

    // === Doubles

    private static final GenericTester.Mode.Builder<Double> DOUBLE = mode("d", c -> (double) c)
            .binary("+", Double::sum)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("count", a -> (double) Long.bitCount(Double.doubleToLongBits(a)))
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;

    // === BigIntegers

    private static final GenericTester.Mode.Builder<BigInteger> BIG_INTEGER = mode("bi", BigInteger::valueOf)
            .binary("+", BigInteger::add)
            .binary("-", BigInteger::subtract)
            .binary("*", BigInteger::multiply)
            .binary("/", BigInteger::divide)
            .unary("-", BigInteger::negate)

            .unary("count", a -> BigInteger.valueOf(a.bitCount()))
            .binary("min", BigInteger::min)
            .binary("max", BigInteger::max)
            ;


    // === Unchecked integers

    private static final GenericTester.Mode.Builder<Integer> INTEGER_UNCHECKED = mode("u", c -> c)
            .binary("+", Integer::sum)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("count", Integer::bitCount)
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;


    // === Short

    private static short s(final int x) {
        return (short) x;
    }

    private static BinaryOperator<Short> s(final IntBinaryOperator op) {
        return (a, b) -> s(op.applyAsInt(a, b));
    }

    private static final GenericTester.Mode.Builder<Short> SHORT = mode("s", c -> (short) c, c -> (short) c)
            .binary("+", s(Integer::sum))
            .binary("-", s((a, b) -> a - b))
            .binary("*", s((a, b) -> a * b))
            .binary("/", s((a, b) -> a / b))
            .unary("-", a -> s(-a))

            .unary("count", a -> s(Integer.bitCount(a & 0xffff)))
            .binary("min", s(Math::min))
            .binary("max", s(Math::max))
            ;

    // == Floats

    private static BinaryOperator<Float> f(final IntPredicate p) {
        return (a, b) -> p.test(a.compareTo(b)) ? 1.0f : 0.0f;
    }


    private static final GenericTester.Mode.Builder<Float> FLOAT = mode("f", c -> (float) c)
            .binary("+", Float::sum)
            .binary("-", (a, b) -> a - b)
            .binary("*", (a, b) -> a * b)
            .binary("/", (a, b) -> a / b)
            .unary("-", a -> -a)

            .unary("count", a -> (float) Integer.bitCount(Float.floatToIntBits(a)))
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;




    // === Truncated integers

    /* package-private */ static final int TRUNCATE = 10;
    private static int it(final int v) {
        return v / TRUNCATE * TRUNCATE;
    }
    private static final GenericTester.Mode.Builder<Integer> INTEGER_TRUNCATE = mode("it", GenericTest::it)
            .binary("+", (a, b) -> it(a + b))
            .binary("-", (a, b) -> it(a - b))
            .binary("*", (a, b) -> it(a * b))
            .binary("/", (a, b) -> it(a / b))
            .unary("-", a -> it(-a))

            .unary("count", a -> it(Integer.bitCount(a)))
            .binary("min", Math::min)
            .binary("max", Math::max)
            ;


    // === Common

    private GenericTest() {
    }

    /* package-private */ static Consumer<GenericTester> unary(final String name) {
        return tester -> tester.unary(name, 1);
    }

    /* package-private */ static Consumer<GenericTester> binary(final String name, final int priority) {
        return tester -> tester.binary(name, priority);
    }

    public static final Selector SELECTOR = Selector.composite(GenericTest.class, GenericTester::new, "easy", "hard")
            .variant("Base", INTEGER_CHECKED, DOUBLE, BIG_INTEGER, ADD, SUBTRACT, MULTIPLY, DIVIDE, NEGATE)
            .variant("4142", INTEGER_UNCHECKED, SHORT, FLOAT, COUNT, MIN, MAX)
            .variant("4749", INTEGER_UNCHECKED, SHORT, FLOAT, MIN, MAX)
            .selector();

    private static <T> GenericTester.Mode.Builder<T> mode(final String mode, final IntFunction<T> constant) {
        return GenericTester.Mode.builder(mode, constant, IntUnaryOperator.identity());
    }

    private static <T> GenericTester.Mode.Builder<T> mode(final String mode, final IntFunction<T> constant, final IntUnaryOperator fixer) {
        return GenericTester.Mode.builder(mode, constant, fixer);
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

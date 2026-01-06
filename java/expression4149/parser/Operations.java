package expression.parser;

import expression.ToMiniString;
import expression.common.ExpressionKind;
import expression.common.Reason;

import java.math.BigInteger;
import java.util.function.*;
import java.util.stream.LongStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Operations {
    // === Base

    public static final Operation NEGATE = unary("-", 1, a -> -a);
    @SuppressWarnings("Convert2MethodRef")
    public static final Operation ADD       = binary("+", 1600, (a, b) -> a + b);
    public static final Operation SUBTRACT  = binary("-", 1602, (a, b) -> a - b);
    public static final Operation MULTIPLY  = binary("*", 2001, (a, b) -> a * b);
    public static final Operation DIVIDE    = binary("/", 2002, (a, b) -> b == 0 ? Reason.DBZ.error() : a / b);

    // === Bitwise operations

    public static final Operation NOT = unary("~", 1, a -> ~a);
    public static final Operation AND = binary("&", 800, (a, b) -> a & b);
    public static final Operation XOR = binary("^", 760, (a, b) -> a ^ b);
    public static final Operation OR = binary("|", 720, (a, b) -> a | b);

    // === Shifts

    @SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
    public static final Operation SHIFT_L = binary("<<", 1202, (a, b) -> (int) a << (int) b);
    public static final Operation SHIFT_A = binary(">>", 1202, (a, b) -> (int) a >> (int) b);
    public static final Operation SHIFT_R = binary(">>>", 1202, (a, b) -> (int) a >>> (int) b);

    // === GCD, LCM

    public static final Operation GCD = binary("gcd", 601, Operations::gcd);
    public static final Operation LCM = binary("lcm", 601, (a, b) -> {
        if (a == 0 || b == 0) {
            return 0;
        }
        return a * b / gcd(a, b);
    });

    private static long gcd(final long a, final long b) {
        return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValue();
    }


    // === MinMax
    public static final Operation MIN = binary("min", 401, Math::min);
    public static final Operation MAX = binary("max", 401, Math::max);


    // === Reverse

    private static Operation digits(final String name, final boolean mask, final int r, final LongBinaryOperator q) {
        return unary(name, 1, v -> LongStream.iterate(mask ? v & 0xffff_ffffL : v, n -> n != 0, n -> n / r)
                .map(n -> n % r)
                .reduce(0, q));
    }

    public static final Operation REVERSE = digits("reverse", false, 10, (a, b) -> a * 10 + b);


    // === Digits
    public static final Operation DIGITS = digits("digits", false, 10, Long::sum);


    // === Floor and Ceiling

    private static long floor(final long a) {
        return (a >= 0 ? a : a - FLOOR_CEILING_STEP + 1) / FLOOR_CEILING_STEP * FLOOR_CEILING_STEP;
    }

    private static long ceiling(final long a) {
        return (a >= 0 ? a + FLOOR_CEILING_STEP - 1: a) / FLOOR_CEILING_STEP * FLOOR_CEILING_STEP;
    }

    public static final int FLOOR_CEILING_STEP = 1000;
    public static final Operation FLOOR = unary("floor", 1, Operations::floor);
    public static final Operation CEILING = unary("ceiling", 1, Operations::ceiling);


    // === Set, Clear

    @SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
    public static final Operation SET = binary("set", 202, (a, b) -> a | (1 << b));
    @SuppressWarnings("IntegerMultiplicationImplicitCastToLong")
    public static final Operation CLEAR = binary("clear", 202, (a, b) -> a & ~(1 << b));


    // === Pow, Log
    public static final Operation POW_O = binary("**", 2402, (a, b) ->
            b < 0 ? 1 : BigInteger.valueOf(a).modPow(BigInteger.valueOf(b), BigInteger.valueOf(1L << 32)).intValue());
    public static final Operation LOG_O = binary("//", 2402, (a, b) ->
            a == 0 && b > 0 ? Integer.MIN_VALUE :
                    a <= 0 || b <= 0 || a == 1 && b == 1 ? 0 :
                            a > 1 && b == 1 ? Integer.MAX_VALUE
                                    : LongStream.iterate(b, v -> v <= a, v -> v * b).count()
    );

    private static final Reason INVALID_POW = new Reason("Invalid power");
    public static final Operation POW = binary("**", 2402, Operations::powC);

    private static long powC(final long a, final long b) {
        if (b < 0 || a == 0 && b == 0) {
            return INVALID_POW.error();
        }
        if (Math.abs(a) > 1 && b > 32) {
            return Reason.OVERFLOW.error();
        }
        final BigInteger result = BigInteger.valueOf(a).pow((int) b);
        if (result.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 || BigInteger.valueOf(Integer.MAX_VALUE).compareTo(result) < 0) {
            return Reason.OVERFLOW.error();
        }
        return result.intValue();
    }

    private static final Reason INVALID_LOG = new Reason("Invalid log");
    public static final Operation LOG = binary("//", 2402, (a, b) ->
            a <= 0 || b <= 1 ? INVALID_LOG.error() : (int) (Math.log(a) / Math.log(b)));


    // Pow10, Log10

    private static final Reason NEG_LOG = new Reason("Logarithm of negative value");
    public static final Operation LOG_2
            = unary("log₂", 1, NEG_LOG.less(1, a-> (long) (Math.log(a) / Math.log(2))));

    private static final Reason NEG_POW = new Reason("Exponentiation to negative power");
    public static final Operation POW_2
            = unary("pow₂", 1, NEG_POW.less(0, Reason.OVERFLOW.greater(31, a -> (long) Math.pow(2, a))));


    // === High, Low
    public static final Operation HIGH = unary("high", 1, v -> Integer.highestOneBit((int) v));
    public static final Operation LOW = unary("low", 1, v -> Integer.lowestOneBit((int) v));


    // === Common

    private Operations() {
    }

    public static Operation unary(final String name, final int priority, final LongUnaryOperator op) {
        return unary(name, priority, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String left, final String right, final LongUnaryOperator op) {
        return unary(left, right, (a, c) -> op.applyAsLong(a));
    }

    public static Operation unary(final String name, final int priority, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(name, priority, op);
    }

    public static Operation unary(final String left, final String right, final BiFunction<Long, LongToIntFunction, Long> op) {
        return tests -> tests.unary(left, right, op);
    }

    public static Operation binary(final String name, final int priority, final LongBinaryOperator op) {
        return tests -> tests.binary(name, priority, op);
    }

    public static <E extends ToMiniString, C> Operation kind(
            final ExpressionKind<E, C> kind,
            final ParserTestSet.Parser<E> parser
    ) {
        return factory -> factory.kind(kind, parser);
    }

    @FunctionalInterface
    public interface Operation extends Consumer<ParserTester> {}
}

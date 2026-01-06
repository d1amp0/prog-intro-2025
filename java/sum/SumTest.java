package sum;

import base.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class SumTest {
    // === Base

    @FunctionalInterface
    /* package-private */ interface Op<T extends Number> extends UnaryOperator<SumTester<T>> {}

    private static final BiConsumer<Number, String> TO_STRING = (expected, out) -> Asserts.assertEquals("Sum", expected.toString(), out);

    private static final Named<Supplier<SumTester<Integer>>> BASE = Named.of("", () -> new SumTester<>(
            Integer::sum, n -> (int) n, (r, max) -> r.nextInt() % max, TO_STRING,
            10, 100, Integer.MAX_VALUE
    ));

    /* package-private */ static <T extends Number> Named<Op<T>> plain() {
        return Named.of("", test -> test);
    }


    // === DoubleHex

    private static BiConsumer<Number, String> approximate(final Function<String, Number> parser, final double precision) {
        return (expected, out) ->
                Asserts.assertEquals("Sum", expected.doubleValue(), parser.apply(out).doubleValue(), precision);
    }

    private static final Named<Supplier<SumTester<Double>>> DOUBLE = Named.of("Double", () -> new SumTester<>(
            Double::sum, n -> (double) n, (r, max) -> (r.getRandom().nextDouble() - 0.5) * 2 * max,
            approximate(Double::parseDouble, 1e-10),
            10.0, 0.01, 1e20, 1e100, Double.MAX_VALUE / 10000)
            .test(5, "2.5 2.5")
            .test(0, "1e100 -1e100")
            .testT(2e100, "1.5e100 0.5e100"));

    private static <T extends Number> Named<Op<T>> hexFull(final Function<T, String> toHex) {
        final Function<T, String> toHexSpoiled = toHex.andThen(s ->s.chars()
                .map(ch -> ((ch ^ s.hashCode()) & 1) == 0 ? Character.toLowerCase(ch) : Character.toUpperCase(ch))
                .mapToObj(Character::toString)
                .collect(Collectors.joining()));
        return Named.of("Hex", test -> test
                .test(toHex, 1)
                .test(toHex, 0x1a)
                .test(toHexSpoiled, 0xA2)
                .test(toHexSpoiled, 0X0, 0X1, 0XF, 0XF, 0x0, 0x1, 0xF, 0xf)
                .test(toHexSpoiled, 0x12345678)
                .test(toHexSpoiled, 0x09abcdef)
                .test(toHexSpoiled, 0x3CafeBab)
                .test(toHexSpoiled, 0x3DeadBee)

                .test(toHex, Integer.MAX_VALUE)
                .test(toHex, Integer.MIN_VALUE)
                .setToString(number -> {
                    final int hashCode = number.hashCode();
                    if ((hashCode & 1) == 0) {
                        return number.toString();
                    }

                    return toHexSpoiled.apply(number);
                })
        );
    }

    // === Octal
    private static <T extends Number> Named<Op<T>> octal(final Function<T, String> toOctal) {
        //noinspection OctalInteger,StringConcatenationMissingWhitespace
        return Named.of("Octal", test -> test
                .test(1, "1o")
                .test(017, "17o")
                .testSpaces(6, " 1o 2o 3O ")
                .test(01234567, "1234567O")

                .test(Integer.MIN_VALUE, "-0" + String.valueOf(Integer.MIN_VALUE).substring(1))
                .test(Integer.MAX_VALUE, "0" + Integer.MAX_VALUE)
                .test(Integer.MAX_VALUE, Integer.toOctalString(Integer.MAX_VALUE) + "o")
                .test(Integer.MAX_VALUE, "0" + Integer.toOctalString(Integer.MAX_VALUE) + "O")
                .setToString(number -> {
                    final int hashCode = number.hashCode();
                    if ((hashCode & 1) == 0) {
                        return number.toString();
                    }

                    final String lower = toOctal.apply(number).toLowerCase(Locale.ROOT) + "o";
                    return (hashCode & 2) == 0 ? lower : lower.toUpperCase(Locale.ROOT);
                })
        );
    }

    // === Long

    private static final Named<Supplier<SumTester<Long>>> LONG = Named.of("Long", () -> new SumTester<>(
            Long::sum, n -> n, (r, max) -> r.getRandom().nextLong() % max, TO_STRING,
            10L, 100L, (long) Integer.MAX_VALUE, Long.MAX_VALUE)
            .test(12345678901234567L, " +12345678901234567 ")
            .test(0L, " +12345678901234567 -12345678901234567")
            .test(0L, " +12345678901234567 -12345678901234567"));

    // === BigInteger

    private static final Named<Supplier<SumTester<BigInteger>>> BIG_INTEGER = Named.of("BigInteger", () -> new SumTester<>(
            BigInteger::add, BigInteger::valueOf, (r, max) -> new BigInteger(max.bitLength(), r.getRandom()), TO_STRING,
            BigInteger.TEN, BigInteger.TEN.pow(10), BigInteger.TEN.pow(100), BigInteger.TWO.pow(1000))
            .test(0, "10000000000000000000000000000000000000000 -10000000000000000000000000000000000000000"));


    // === BigDecimalHex

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private static final Named<Supplier<SumTester<BigDecimal>>> BIG_DECIMAL = Named.of("BigDecimal", () -> new SumTester<>(
            BigDecimal::add, BigDecimal::valueOf,
            (r, max) -> {
                final BigInteger unscaled = new BigInteger((max.precision() - max.scale() + 2) * 3, r.getRandom());
                return new BigDecimal(unscaled, 3);
            },
            TO_STRING,
            BigDecimal.TEN, BigDecimal.TEN.pow(10), BigDecimal.TEN.pow(100), BigDecimal.ONE.add(BigDecimal.ONE).pow(1000))
            .testT(BigDecimal.ZERO.setScale(3), "10000000000000000000000000000000000000000.123 -10000000000000000000000000000000000000000.123"));

    private static String bigDecimalToString(final BigDecimal number) {
        final int scale = number.scale();
        return "0x" + number.unscaledValue().toString(16) + (scale == 0 ? "" : "s" + Integer.toString(scale, 16));
    }


    // === Hex

    private static <T extends Number> Named<Op<T>> hex(final Function<T, String> toHex) {
        return hexFull(v -> "0x" + toHex.apply(v));
    }


    // === Common

    /* package-private */ static <T extends Number> Consumer<TestCounter> variant(
            final Named<Function<String, Runner>> runner,
            final Named<Supplier<SumTester<T>>> test,
            final Named<? extends Function<? super SumTester<T>, ? extends SumTester<?>>> modifier
    ) {
        return counter -> modifier.value().apply(test.value().get())
                .test("Sum" + test.name() + modifier.name() + runner.name(), counter, runner.value());
    }

    /* package-private */ static final Named<Function<String, Runner>> RUNNER =
            Named.of("", Runner.packages("", "sum")::args);

    public static final Selector SELECTOR = selector(SumTest.class, RUNNER);

    private SumTest() {
        // Utility class
    }

    public static Selector selector(final Class<?> owner, final Named<Function<String, Runner>> runner) {
        return new Selector(owner)
                .variant("Base",            variant(runner, BASE, plain()))
                .variant("3637",            variant(runner, DOUBLE, hexFull(value -> value == value.intValue() && value > 0 ? "0x" + Integer.toHexString(value.intValue()) : Double.toHexString(value))))
                .variant("3839",            variant(runner, BIG_DECIMAL, hexFull(SumTest::bigDecimalToString)))
                .variant("3435",            variant(runner, BASE, hex(Integer::toHexString)))
                .variant("3233",            variant(runner, DOUBLE, plain()))
                .variant("4749",            variant(runner, LONG, octal(Long::toOctalString)))
                .variant("4142",            variant(runner, BIG_INTEGER, octal(number -> number.toString(8))))
                ;
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

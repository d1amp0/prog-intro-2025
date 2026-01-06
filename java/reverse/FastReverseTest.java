package reverse;

import base.ExtendedRandom;
import base.Named;
import base.Selector;
import base.TestCounter;
import wordStat.WordStatTest;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.LongBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class FastReverseTest {
    // === 3637

    private static final Named<BiFunction<ExtendedRandom, Integer, String>> OCT = Named.of("",
            (r, i) -> r.nextBoolean() ? Integer.toString(i) : Integer.toOctalString(i) + (r.nextBoolean() ? "o" : "O")
    );
    private static final Named<BiFunction<ExtendedRandom, Integer, String>> DEC = Named.of("", (r, i) -> Integer.toString(i));

    private static final Named<String> PUNCT = Named.of(
            "",
            IntStream.range(0, Character.MAX_VALUE)
                    .filter(ch -> ch == ' ' || Character.getType(ch) == Character.START_PUNCTUATION || Character.getType(ch) == Character.END_PUNCTUATION)
                    .filter(ch -> ch != 13 && ch != 10)
                    .mapToObj(Character::toString)
                    .collect(Collectors.joining())
    );

    public static final Named<ReverseTester.Op> MIN_C = Named.of("MinC", scan2((a, b) -> b));
    public static final Named<ReverseTester.Op> MIN = Named.of("Min", scan2(Math::min));

    private static ReverseTester.Op scan2(final LongBinaryOperator reduce) {
        return ints -> {
            // This code is intentionally obscure
            final int length = Arrays.stream(ints).mapToInt(r -> r.length).max().orElse(0);
            final long[] cs = new long[length];
            final long[] cc = new long[length + 1];
            Arrays.fill(cs, Integer.MAX_VALUE);
            Arrays.fill(cc, Integer.MAX_VALUE);
            //noinspection NestedAssignment
            final long[][] rows = range(ints.length).mapToObj(i -> {
                        range(ints[i].length).forEachOrdered(j -> cc[j] = reduce.applyAsLong(
                                cc[j + 1],
                                cs[j] = Math.min(cs[j], ints[i][j])
                        ));
                        return Arrays.copyOf(cc, ints[i].length);
                    })
                    .toArray(long[][]::new);
            return range(ints.length).mapToObj(i -> rows[i]).toArray(long[][]::new);
        };
    }

    private static IntStream range(final int length) {
        return IntStream.iterate(length - 1, i -> i >= 0, i -> i - 1);
    }
    

    // === Common

    public static final int MAX_SIZE = 1_000_000 / TestCounter.DENOMINATOR / TestCounter.DENOMINATOR;

    public static final Selector SELECTOR = new Selector(FastReverseTest.class)
            .variant("Base", ReverseTester.variant(MAX_SIZE, ReverseTest.REVERSE))
            .variant("3637", ReverseTester.variant(MAX_SIZE, "", MIN_C, OCT, DEC, PUNCT))
            .variant("3839", ReverseTester.variant(MAX_SIZE, "", MIN, OCT, DEC, PUNCT))
            .variant("3435", ReverseTester.variant(MAX_SIZE, ReverseTest.ROTATE, PUNCT))
            .variant("3233", ReverseTester.variant(MAX_SIZE, ReverseTest.EVEN, PUNCT))
            .variant("4142", ReverseTester.variant(MAX_SIZE, ReverseTest.AVG, PUNCT))
            .variant("4749", ReverseTester.variant(MAX_SIZE, ReverseTest.SUM, PUNCT))

            ;


    private FastReverseTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
        WordStatTest.main(args);
    }
}

package reverse;

import base.Named;
import base.Selector;
import base.TestCounter;
import reverse.ReverseTester.Op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntToLongFunction;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

/**
 * Tests for {@code Reverse} homework.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ReverseTest {
    // === Base
    public static final Named<Op> REVERSE = Named.of("", ReverseTester::transform);


    // === Max

    public static final Named<Op> MAX_C = Named.of("MaxC", scan2((a, b) -> b));
    public static final Named<Op> MAX = Named.of("Max", scan2(Math::max));

    private static Op scan2(final LongBinaryOperator reduce) {
        return ints -> {
            // This code is intentionally obscure
            final int length = Arrays.stream(ints).mapToInt(r -> r.length).max().orElse(0);
            final long[] cs = new long[length];
            final long[] cc = new long[length + 1];
            Arrays.fill(cs, Integer.MIN_VALUE);
            Arrays.fill(cc, Integer.MIN_VALUE);
            //noinspection NestedAssignment
            final long[][] rows = range(ints.length).mapToObj(i -> {
                        range(ints[i].length).forEachOrdered(j -> cc[j] = reduce.applyAsLong(
                                cc[j + 1],
                                cs[j] = Math.max(cs[j], ints[i][j])
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


    // === Rotate
    public static final Named<Op> ROTATE = Named.of("Rotate", ints -> {
        final List<int[]> rows = new ArrayList<>(List.of(ints));
        return IntStream.range(0, Arrays.stream(ints).mapToInt(r -> r.length).max().orElse(0))
                .mapToObj(c -> {
                    rows.removeIf(r -> r.length <= c);
                    return range(rows.size()).mapToObj(rows::get).mapToLong(r -> r[c]).toArray();
                })
                .toArray(long[][]::new);
    });


    // === Even
    public static final Named<Op> EVEN = Named.of(
            "Even",
            ints -> ReverseTester.transform(IntStream.range(0, ints.length)
                    .mapToObj(i -> IntStream.range(0, ints[i].length)
                            .filter(j -> (i + j) % 2 == 0)
                            .map(j -> ints[i][j]))
                    .map(IntStream::toArray).toArray(int[][]::new))
    );

    // Sum
    @FunctionalInterface
    interface LongTernaryOperator {
        long applyAsLong(long a, long b, long c);
    }
    
    public static final Named<Op> SUM = cross("Sum", 0, Long::sum, (r, c, v) -> r + c - v);

    private static long[][] cross(
            final int[][] ints,
            final IntToLongFunction map,
            final LongBinaryOperator reduce,
            final int zero,
            final LongTernaryOperator get
    ) {
        // This code is intentionally obscure
        final long[] rt = Arrays.stream(ints)
                .map(Arrays::stream)
                .mapToLong(row -> row.mapToLong(map).reduce(zero, reduce))
                .toArray();
        final long[] ct = new long[Arrays.stream(ints).mapToInt(r -> r.length).max().orElse(0)];
        Arrays.fill(ct, zero);
        Arrays.stream(ints).forEach(r -> IntStream.range(0, r.length)
                .forEach(i -> ct[i] = reduce.applyAsLong(ct[i], map.applyAsLong(r[i]))));
        return IntStream.range(0, ints.length)
                .mapToObj(r -> IntStream.range(0, ints[r].length)
                        .mapToLong(c -> get.applyAsLong(rt[r], ct[c], ints[r][c]))
                        .toArray())
                .toArray(long[][]::new);
    }

    private static Named<Op> cross(
            final String name,
            final int zero,
            final LongBinaryOperator reduce,
            final LongTernaryOperator get
    ) {
        return Named.of(name, ints -> cross(ints, n -> n, reduce, zero, get));
    }

    public static final Named<Op> AVG = avg(
            "Avg",
            ints -> cross(ints, n -> n, Long::sum, 0, (r, c, v) -> r + c - v),
            ints -> cross(ints, n -> 1, Long::sum, 0, (r1, c1, v1) -> r1 + c1 - 1)
    );

    private static Named<Op> avg(
            final String name,
            final Op fs,
            final Op fc
    ) {
        return Named.of(name, ints -> avg(ints, fs.apply(ints), fc.apply(ints)));
    }

    private static long[][] avg(final int[][] ints, final long[][] as, final long[][] ac) {
        return IntStream.range(0, ints.length).mapToObj(i -> IntStream.range(0, ints[i].length)
                        .mapToLong(j -> as[i][j] / ac[i][j])
                        .toArray())
                .toArray(long[][]::new);
    }


    // === Common

    public static final int MAX_SIZE = 10_000 / TestCounter.DENOMINATOR;

    public static final Selector SELECTOR = selector(ReverseTest.class, MAX_SIZE);

    private ReverseTest() {
        // Utility class
    }

    public static Selector selector(final Class<?> owner, final int maxSize) {
        return new Selector(owner)
                .variant("Base",        ReverseTester.variant(maxSize, REVERSE))
                .variant("3637",        ReverseTester.variant(maxSize, MAX_C))
                .variant("3839",        ReverseTester.variant(maxSize, MAX))
                .variant("3435",        ReverseTester.variant(maxSize, ROTATE))
                .variant("3233",        ReverseTester.variant(maxSize, EVEN))
                .variant("4142",        ReverseTester.variant(maxSize, AVG))
                .variant("4749",        ReverseTester.variant(maxSize, SUM))
                ;
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

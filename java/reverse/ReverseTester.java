package reverse;

import base.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class ReverseTester {

    public static final Named<Op> TRANSFORM = Named.of("", ReverseTester::transform);
    public static final Named<String> SPACE = Named.of("", " ");

    @FunctionalInterface
    public interface Op extends Function<int[][], long[][]> {}

    private static final int[] DIVISORS = {100, 10, 1};

    private final Op transform;
    private final BiFunction<ExtendedRandom, Integer, String> inputToString;
    private final BiFunction<ExtendedRandom, Integer, String> outputToString;
    private final String name;
    private final String spaces;

    private ReverseTester(final String className, final Op transform, final String spaces) {
        this(className, transform, spaces, (r, i) -> Integer.toString(i), (r, i) -> Long.toString(i));
    }

    private ReverseTester(
            final String className,
            final Op transform,
            final String spaces,
            final BiFunction<ExtendedRandom, Integer, String> inputToString,
            final BiFunction<ExtendedRandom, Integer, String> outputToString
    ) {
        name = className;
        this.transform = transform;
        this.spaces = spaces;
        this.inputToString = inputToString;
        this.outputToString = outputToString;
    }

    private static Consumer<TestCounter> variant(final int maxSize, final Supplier<ReverseTester> tester) {
        return counter -> tester.get().run(counter, maxSize);
    }

    public static Consumer<TestCounter> variant(final int maxSize, final Named<Op> transform) {
        return variant(maxSize, transform, SPACE);
    }


    public static Consumer<TestCounter> variant(final int maxSize, final Named<Op> transform, final Named<String> spaces) {
        Objects.requireNonNull(transform);
        Objects.requireNonNull(spaces);
        return variant(
                maxSize,
                () -> new ReverseTester("Reverse" + transform.name() + spaces.name(), transform.value(), spaces.value())
        );
    }

    public static Consumer<TestCounter> variant(
            final int maxSize,
            final String suffix,
            final Named<BiFunction<ExtendedRandom, Integer, String>> input,
            final Named<BiFunction<ExtendedRandom, Integer, String>> output
    ) {
        return variant(maxSize, suffix, TRANSFORM, input, output);
    }

    public static Consumer<TestCounter> variant(
            final int maxSize,
            final String suffix,
            final Named<Op> op,
            final Named<BiFunction<ExtendedRandom, Integer, String>> input,
            final Named<BiFunction<ExtendedRandom, Integer, String>> output
    ) {
        return variant(maxSize, suffix, op, input, output, SPACE);
    }

    public static Consumer<TestCounter> variant(
            final int maxSize,
            final String suffix,
            final Named<Op> op,
            final Named<BiFunction<ExtendedRandom, Integer, String>> input,
            final Named<BiFunction<ExtendedRandom, Integer, String>> output,
            final Named<String> spaces
    ) {
        final String out = input.name().contains(output.name()) ? "" : output.name();
        return variant(maxSize, () -> new ReverseTester(
                "Reverse" + op.name() + input.name() + out + suffix + spaces.name(),
                op.value(),
                spaces.value(),
                input.value(),
                output.value()
        ));
    }

    private void run(final TestCounter counter, final int maxSize) {
        new Checker(counter, maxSize, Runner.packages("", "reverse").std(name), spaces).test();
    }

    @Override
    public String toString() {
        return name;
    }

    public static long[][] transform(final int[][] ints) {
        return IntStream.range(1, ints.length + 1)
                .mapToObj(i -> ints[ints.length - i])
                .map(is -> IntStream.range(1, is.length + 1).mapToLong(i -> is[is.length - i]).toArray())
                .toArray(long[][]::new);
    }

    /**
     * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
     */
    private class Checker extends BaseChecker {
        private final int maxSize;
        private final Runner runner;
        private final String spaces;
        private final Set<String> manualTests = new HashSet<>();

        Checker(final TestCounter counter, final int maxSize, final Runner runner, final String spaces) {
            super(counter);
            this.maxSize = maxSize;
            this.runner = runner;
            this.spaces = spaces;
        }

        public void manualTest(final int[][] ints) {
            for (final List<int[]> permutation : permutations(new ArrayList<>(Arrays.asList(ints)))) {
                final int[][] input = permutation.toArray(int[][]::new);
                final String[][] lines = toString(input, inputToString);
                if (manualTests.add(Arrays.deepToString(lines))) {
                    test(lines, toString(transform.apply(input), outputToString));
                }
            }
        }

        public void test(final int[][] ints) {
            test(toString(ints, inputToString), toString(transform.apply(ints), outputToString));
        }

        public void test(final String[][] input, final String[][] output) {
            final List<String> inputLines = toLines(input, random().randomString(spaces, 1, 10));
            final List<String> outputLines = toLines(output, " ");
            runner.testEquals(counter, inputLines, outputLines);
        }

        private String[][] toString(final int[][] ints, final BiFunction<ExtendedRandom, Integer, String> toString) {
            return Arrays.stream(ints)
                    .map(row -> Arrays.stream(row).mapToObj(i -> toString.apply(random(), i)).toArray(String[]::new))
                    .toArray(String[][]::new);
        }

        private String[][] toString(final long[][] ints, final BiFunction<ExtendedRandom, Integer, String> toString) {
            return Arrays.stream(ints)
                    .map(row -> Arrays.stream(row).mapToObj(i -> toString.apply(random(), (int) i)).toArray(String[]::new))
                    .toArray(String[][]::new);
        }

        private List<String> toLines(final String[][] data, final String delimiter) {
            if (data.length == 0) {
                return Collections.singletonList("");
            }
            return Arrays.stream(data)
                    .map(row -> String.join(delimiter, row))
                    .collect(Collectors.toList());
        }

        public int[][] random(final int[] profile) {
            final int col = random().nextInt(Arrays.stream(profile).max().orElse(0));
            final int row = random().nextInt(profile.length);
            final int m = random().nextInt(5) - 2;
            final int[][] ints = Arrays.stream(profile).mapToObj(random().getRandom()::ints).map(IntStream::toArray).toArray(int[][]::new);
            Arrays.stream(ints).filter(r -> col < r.length).forEach(r -> r[col] = Math.abs(r[col]) / 2 * m);
            ints[row] = Arrays.stream(ints[row]).map(Math::abs).map(v -> v / 2 * m).toArray();
            return ints;
        }

        public void test() {
            manualTest(new int[][]{
                    {1}
            });
            manualTest(new int[][]{
                    {1, 2},
                    {3}
            });
            manualTest(new int[][]{
                    {1, 2, 3},
                    {4, 5},
                    {6}
            });
            manualTest(new int[][]{
                    {1, 2, 3},
                    {},
                    {4, 5},
                    {6}
            });
            manualTest(new int[][]{
                    {1, 2, 3},
                    {-4, -5},
                    {6}
            });
            manualTest(new int[][]{
                    {1, -2, 3},
                    {},
                    {4, -5},
                    {6}
            });
            manualTest(new int[][]{
                    {1, 2, 0},
                    {1, 0},
                    {0},
            });
            manualTest(new int[][]{
                    {1},
                    {1, 3},
                    {1, 2, 3},
            });
            manualTest(new int[][]{
                    {-1},
                    {-1, -2},
                    {-1, -2, -3},
            });
            manualTest(new int[][]{
                    {},
            });
            manualTest(new int[][]{
                    {},
                    {},
                    {},
            });
            testRandom(tweakProfile(constProfile(10, 10), new int[][]{}));
            testRandom(tweakProfile(constProfile(100, 100), new int[][]{}));
            testRandom(randomProfile(100, maxSize));
            testRandom(randomProfile(maxSize / 10, maxSize));
            testRandom(randomProfile(maxSize, maxSize));
            for (final int d : DIVISORS) {
                final int size = maxSize / d;
                testRandom(tweakProfile(constProfile(size / 2, 0), new int[][]{{size / 2, 0}}));
                testRandom(tweakProfile(randomProfile(size, size / 2), new int[][]{{size / 2, 0}}));
                testRandom(tweakProfile(constProfile(size / 2, 0), new int[][]{{size / 2, size / 2 - 1}}));
                testRandom(tweakProfile(constProfile(size / 3, 1), new int[][]{{size / 3, size / 6, size / 3 - 1}}));
            }
        }

        private int[] randomProfile(final int length, final int values) {
            final int[] profile = new int[length];
            for (int i = 0; i < values; i++) {
                profile[random().nextInt(0, length - 1)]++;
            }
            return profile;
        }

        private void testRandom(final int[] profile) {
            test(random(profile));
        }

        private int[] constProfile(final int length, final int value) {
            final int[] profile = new int[length];
            Arrays.fill(profile, value);
            return profile;
        }

        private int[] tweakProfile(final int[] profile, final int[][] mods) {
            for (final int[] mod : mods) {
                Arrays.stream(mod).skip(1).forEach(i -> profile[i] = mod[0]);
            }
            return profile;
        }
    }

    private static <T> List<List<T>> permutations(final List<T> elements) {
        final List<List<T>> result = new ArrayList<>();
        permutations(new ArrayList<>(elements), result, elements.size() - 1);
        return result;
    }

    private static <T> void permutations(final List<T> elements, final List<List<T>> result, final int n) {
        if (n == 0) {
            result.add(List.copyOf(elements));
        } else {
            for (int i = 0; i < n; i++) {
                permutations(elements, result, n - 1);
                if (n % 2 == 1) {
                    Collections.swap(elements, i, n);
                } else {
                    Collections.swap(elements, 0, n);
                }
            }
            permutations(elements, result, n - 1);
        }
    }
}

package wordStat;

import base.Named;
import base.Selector;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests for <a href="https://www.kgeorgiy.info/courses/prog-intro/homeworks.html#wordstat">Word Statistics</a> homework
 * of <a href="https://www.kgeorgiy.info/courses/prog-intro/">Introduction to Programming</a> course.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class WordStatTest {
    // === Base
    private static final Named<Function<String, Stream<String>>> ID  = Named.of("", Stream::of);
    private static final WordStatTester.Variant BASE = new WordStatTester.Variant("", false, Comparator.comparingInt(p -> 0));


    // === 3637
    public static final int SIZE = 3;
    private static final WordStatTester.Variant LENGTH = new WordStatTester.Variant("Length", false, Comparator.comparingInt(p -> p.first().length()));
    private static final Named<Function<String, Stream<String>>> MIDDLE =
            size("Middle", SIZE * 2 + 1, s -> Stream.of(s.substring(SIZE, s.length() - SIZE)));

    static Named<Function<String, Stream<String>>> size(
            final String name,
            final int length,
            final Function<String, Stream<String>> f
    ) {
        return Named.of(name, s -> s.length() >= length ? f.apply(s) : Stream.empty());
    }

    // === 3839
    private static final Named<Function<String, Stream<String>>> AFFIX = size(
            "Affix",
            2,
            s -> Stream.of(s.substring(0, s.length() / 2), s.substring(s.length() - s.length() / 2))
    );

    // === 3536
    private static final Named<Function<String, Stream<String>>> SUFFIX =
            size("Suffix", 2, s -> Stream.of(s.substring(s.length() - s.length() / 2)));

    // === 4749
    private static final Named<Function<String, Stream<String>>> PREFIX =
            size("Prefix", 2, s -> Stream.of(s.substring(0, s.length() / 2)));

    // === Common
    public static final Selector SELECTOR = new Selector(WordStatTester.class)
            .variant("Base", BASE.with(ID))
            .variant("3637", LENGTH.with(MIDDLE))
            .variant("3839", LENGTH.with(AFFIX))
            .variant("3435", LENGTH.with(SUFFIX))
            .variant("3233", LENGTH.with(ID))
            .variant("4142", LENGTH.with(MIDDLE))
            .variant("4749", LENGTH.with(PREFIX))

            ;

    private WordStatTest() {
        // Utility class
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

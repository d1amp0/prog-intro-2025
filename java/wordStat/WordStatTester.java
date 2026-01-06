package wordStat;

import base.ExtendedRandom;
import base.Named;
import base.Pair;
import base.TestCounter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class WordStatTester {
    public static final String PRE_LOWER = chars()
            .filter(s -> s.toLowerCase(Locale.ROOT).length() == 1)
            .collect(Collectors.joining());
    public static final String POST_LOWER = chars()
            .collect(Collectors.joining())
            .toLowerCase();

    private WordStatTester() {
    }

    private static Stream<String> chars() {
        return IntStream.range(' ', Character.MAX_VALUE)
                .filter(ch -> !Character.isSurrogate((char) ch))
                .filter(ch -> Character.getType(ch) != Character.NON_SPACING_MARK)
                .filter(ch -> Character.getType(ch) != Character.DIRECTIONALITY_NONSPACING_MARK)
                .mapToObj(Character::toString);
    }

    /* package-private */ record Variant(String name, boolean reverse, Comparator<Pair<String, Integer>> c) {
        public Consumer<TestCounter> with(final Named<Function<String, Stream<String>>> split) {
            return counter -> WordStatChecker.test(
                    counter,
                    "WordStat" + name + split.name(),
                    text -> answer(split.value(), text),
                    checker -> {
                        checker.test("To be, or not to be, that is the question:");
                        checker.test("Monday's child is fair of face.", "Tuesday's child is full of grace.");
                        checker.test("Шалтай-Болтай", "Сидел на стене.", "Шалтай-Болтай", "Свалился во сне.");
                        checker.test(
                                "27 октября — 300-й день григорианскому календарю. До конца года остаётся 65 дней.",
                                "До 15 октября 1582 года — 27 октября по юлианскому календарю, с 15 октября 1582 года — 27 октября по григорианскому календарю.",
                                "В XX и XXI веках соответствует 14 октября по юлианскому календарю[1].",
                                "(c) Wikipedia"
                        );
                        checker.test("23 октября — Всемирный день психического здоровья", "Тема 2025 года: Психическое здоровье на рабочем месте");

                        checker.randomTest(3, 10, 10, 3, ExtendedRandom.ENGLISH, WordStatChecker.SIMPLE_DELIMITERS);
                        checker.randomTest(10, 3, 5, 5, ExtendedRandom.RUSSIAN, WordStatChecker.SIMPLE_DELIMITERS);
                        checker.randomTest(4, 10, 10, 3, ExtendedRandom.GREEK, WordStatChecker.SIMPLE_DELIMITERS);
                        checker.randomTest(4, 10, 10, 3, WordStatChecker.DASH, WordStatChecker.SIMPLE_DELIMITERS);
                        checker.randomTest(3, 10, 10, 3, ExtendedRandom.ENGLISH, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(10, 3, 5, 5, ExtendedRandom.RUSSIAN, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(3, 10, 10, 3, ExtendedRandom.GREEK, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(3, 10, 10, 3, WordStatChecker.DASH, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(3, 10, 10, 10, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);

                        final int d = TestCounter.DENOMINATOR;
                        final int d2 = TestCounter.DENOMINATOR;
                        checker.randomTest(10, 10000 / d, 10, 10, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(10, 1, 10, 10, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(10, 1000 / d, 100 / d2, 100 / d2, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(4, 1000 / d, 10, 3000 / d, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(4, 1000 / d, 3000 / d, 10, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(10000 / d, 20, 10, 5, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);
                        checker.randomTest(1000000 / d, 2, 2, 1, WordStatChecker.ALL, WordStatChecker.ADVANCED_DELIMITERS);

                        checker.test(PRE_LOWER);
                        checker.test(POST_LOWER);
                    }
            );
        }

        private List<Pair<String, Integer>> answer(final Function<String, Stream<String>> split, final String[][] text) {
            final List<String> parts = Arrays.stream(text)
                    .flatMap(Arrays::stream)
                    .filter(Predicate.not(String::isEmpty))
                    .flatMap(split)
                    .peek(s -> {assert !s.isBlank();})
                    .collect(Collectors.toList());
            if (reverse()) {
                Collections.reverse(parts);
            }
            return parts.stream()
                    .collect(Collectors.toMap(String::toLowerCase, v -> 1, Integer::sum, LinkedHashMap::new))
                    .entrySet().stream()
                    .map(Pair::of)
                    .sorted(c)
                    .toList();
        }
    }
}

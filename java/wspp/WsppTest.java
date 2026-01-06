package wspp;

import base.Named;
import base.Selector;

import java.util.Comparator;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class WsppTest {
    // === Base
    private static final Named<Comparator<Map.Entry<String, Integer>>> INPUT = Named.of("", Comparator.comparingInt(e -> 0));
    private static final Named<IntFunction<IntStream>> ALL = Named.of("", size -> IntStream.range(0, size));
    private static final Named<WsppTester.Extractor<Object>> WSPP = Named.of("", (r, l, L, g, G) -> g);
    private static final Named<String> NONE = Named.of("", "");

    // === 3637
    private static final Named<Comparator<Map.Entry<String, Integer>>> LENGTH = Named.of("",
            Map.Entry.comparingByKey(Comparator.comparingInt(String::length)));
    private static final Named<IntFunction<IntStream>> LAST = Named.of("Last", size -> IntStream.of(size - 1));
    private static final Named<String> JAVA = Named.of("", "XHB7TmR9JF8=");

    // === 3839
    private static final Named<IntFunction<IntStream>> MIDDLE = Named.of("Middle", size -> IntStream.of(size / 2));

    // === 3435
    public static final WsppTester.Extractor<String> POSITION = (r, l, L, g, G) -> r + ":" + (G - g + 1);


    // === Common
    public static final Selector SELECTOR = new Selector(WsppTester.class)
            .variant("Base",            WsppTester.variant(INPUT, ALL, WSPP, NONE))
            .variant("3637",            WsppTester.variant(LENGTH, LAST, WSPP, JAVA))
            .variant("3839",            WsppTester.variant(LENGTH, MIDDLE, WSPP, JAVA))
            .variant("3435",            WsppTester.variant(LENGTH, ALL, Named.of("Position", POSITION), JAVA))
            .variant("3233",            WsppTester.variant(INPUT, ALL, Named.of("Pos", POSITION), JAVA))
            .variant("4142",            WsppTester.variant(LENGTH, LAST, WSPP, JAVA))
            .variant("4749",            WsppTester.variant(LENGTH, ALL, Named.of("Position", POSITION), JAVA))
            ;

    private WsppTest() {
    }

    public static void main(final String... args) {
        SELECTOR.main(args);
    }
}

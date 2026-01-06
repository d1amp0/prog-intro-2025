package expression.common;

import base.Pair;
import base.TestCounter;
import expression.ToMiniString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TestGenerator<C, E extends ToMiniString> {
    private final Generator<C, E> generator;
    private final NodeRenderer<C> renderer;

    public TestGenerator(final Generator<C, E> generator, final NodeRenderer<C> renderer) {
        this.generator = generator;
        this.renderer = renderer;
    }

    public void testBasic(final Consumer<Test<C, E>> test) {
        generator.testBasic(consumer(test));
    }

    public void testRandom(final TestCounter counter, final int denominator, final Consumer<Test<C, E>> test) {
        generator.testRandom(counter, denominator, consumer(test));
    }

    private Consumer<Expr<C, E>> consumer(final Consumer<TestGenerator.Test<C, E>> consumer) {
        return expr -> consumer.accept(new TestGenerator.Test<>(expr, renderer));
    }


    public List<Pair<String, E>> variables(final int count) {
        return generator.variables(count);
    }

    public String render(final Expr<C, ?> expr, final NodeRenderer.Settings settings) {
        return renderer.render(expr, settings);
    }

    public static class Test<C, E> {
        public final Expr<C, E> expr;
        private final Map<NodeRenderer.Settings, String> rendered = new HashMap<>();
        private final NodeRenderer<C> renderer;

        public Test(final Expr<C, E> expr, final NodeRenderer<C> renderer) {
            this.expr = expr;
            this.renderer = renderer;
        }

        public String render(final NodeRenderer.Settings settings) {
            return rendered.computeIfAbsent(settings, s -> renderer.render(expr, s));
        }
    }
}

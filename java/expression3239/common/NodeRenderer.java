package expression.common;

import base.ExtendedRandom;

import java.util.List;
import java.util.Map;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class NodeRenderer<C> {
    public static final String PAREN = "[";
    public static final List<Paren> DEFAULT_PARENS = List.of(paren("(", ")"));

    public static final Mode MINI_MODE = Mode.SIMPLE_MINI; // Replace by TRUE_MINI for some challenge;
    public static final Settings FULL = Mode.FULL.settings(0);
    public static final Settings FULL_EXTRA = Mode.FULL.settings(Integer.MAX_VALUE / 4);
    public static final Settings SAME = Mode.SAME.settings(0);
    public static final Settings MINI = MINI_MODE.settings(0);
    public static final Settings TRUE_MINI = Mode.TRUE_MINI.settings(0);

    private final Renderer<C, Settings, Node<C>> renderer;
    private final Map<String, String> brackets;
    private final ExtendedRandom random;

    public NodeRenderer(
            final Renderer<C, Settings, Node<C>> renderer,
            final Map<String, String> brackets,
            final ExtendedRandom random
    ) {
        this.renderer = renderer;
        this.brackets = Map.copyOf(brackets);
        this.random = random;
    }

    public static <C> Node<C> paren(final boolean condition, final Node<C> node) {
        return condition ? Node.op(PAREN, 1, node) : node;
    }

    public static Paren paren(final String open, final String close) {
        return new Paren(open, close);
    }

    public Node<C> renderToNode(final Settings settings, final Expr<C, ?> expr) {
        final Expr<C, Node<C>> convert = expr.convert((name, variable) -> Node.op(name));
        return renderer.render(convert, settings);
    }

    public String render(final Node<C> node, final List<Paren> parens) {
        return node.cata(
                String::valueOf,
                name -> name,
                (name, priority, arg) ->
                        name == PAREN ? random.randomItem(parens).apply(arg) :
                        priority == Integer.MAX_VALUE ? name + arg + brackets.get(name) :
                        (priority & 1) == 1 ? name + arg :
                        arg + name,
                (name, a, b) -> a + " " + name + " " + b
        );
    }

    public String render(final Expr<C, ?> expr, final Settings settings) {
        return render(renderToNode(settings, expr), settings.parens());
    }

    public enum Mode {
        FULL, SAME, TRUE_MINI, SIMPLE_MINI;

        public Settings settings(final int limit) {
            return new Settings(this, limit);
        }
    }

    public record Paren(String open, String close) {
        String apply(final String expression) {
            return open() + expression + close();
        }
    }

    public record Settings(Mode mode, int limit, List<Paren> parens) {
        public Settings(final Mode mode, final int limit) {
            this(mode, limit, DEFAULT_PARENS);
        }

        public <C> Node<C> extra(Node<C> node, final ExtendedRandom random) {
            while (random.nextInt(Integer.MAX_VALUE) < limit) {
                node = paren(true, node);
            }
            return node;
        }

        public Settings withParens(final List<Paren> parens) {
            return this.parens.equals(parens) ? this : new Settings(mode, limit, List.copyOf(parens));
        }
    }
}

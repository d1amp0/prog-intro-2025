package expression.common;

import base.ExtendedRandom;
import base.Functional;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public class NodeRendererBuilder<C> {
    private final Renderer.Builder<C, NodeRenderer.Settings, Node<C>> nodeRenderer = Renderer.builder(Node::constant);
    private final Map<String, Priority> priorities = new HashMap<>();
    private final Map<String, String> brackets = new HashMap<>();
    private final ExtendedRandom random;

    public NodeRendererBuilder(final ExtendedRandom random) {
        this.random = random;
        nodeRenderer.unary(NodeRenderer.PAREN, (mode, arg) -> NodeRenderer.paren(true, arg));
    }

    public void unary(final String name, final int priority) {
        final String space = name.equals("-") || Character.isLetter(name.charAt(0)) ? " " : "";
        nodeRenderer.unary(
                name,
                (settings, arg) -> settings.extra(Node.op(name, priority, inner(settings, priority, arg, space)), random)
        );
    }

    public void unary(final String left, final String right) {
        brackets.put(left, right);
        nodeRenderer.unary(
                left,
                (settings, arg) -> settings.extra(Node.op(left, Integer.MAX_VALUE, arg), random)
        );
    }

    private Node<C> inner(final NodeRenderer.Settings settings, final int priority, final Node<C> arg, final String space) {
        if (settings.mode() == NodeRenderer.Mode.FULL) {
            return NodeRenderer.paren(true, arg);
        } else {
            final String op = arg.get(
                    c -> space,
                    n -> space,
                    (n, p, a) ->
                            priority > unaryPriority(arg) ? NodeRenderer.PAREN :
                            NodeRenderer.PAREN.equals(n) ? "" :
                            space,
                    (n, a, b) -> NodeRenderer.PAREN
            );
            return op.isEmpty() ? arg : Node.op(op, Priority.MAX.priority | 1, arg);
        }
    }

    private static <C> Integer unaryPriority(final Node<C> node) {
        return node.get(c -> Integer.MAX_VALUE, n -> Integer.MAX_VALUE, (n, p, a) -> p, (n, a, b) -> Integer.MIN_VALUE);
    }

    public void binary(final String name, final int priority) {
        final Priority mp = new Priority(name, priority);
        priorities.put(name, mp);

        nodeRenderer.binary(name, (settings, l, r) -> settings.extra(process(settings, mp, l, r), random));
    }

    private Node<C> process(final NodeRenderer.Settings settings, final Priority mp, final Node<C> l, final Node<C> r) {
        if (settings.mode() == NodeRenderer.Mode.FULL) {
            return NodeRenderer.paren(true, op(mp, l, r));
        }

        final Priority lp = priority(l);
        final Priority rp = priority(r);

        final int rc = rp.compareLevels(mp);

        // :NOTE: Especially ugly code, do not replicate
        final boolean advanced = settings.mode() == NodeRenderer.Mode.SAME
                || mp.has(2)
                || mp.has(1) && (mp != rp || (settings.mode() == NodeRenderer.Mode.TRUE_MINI && hasOther(r, rp)));

        final Node<C> al = NodeRenderer.paren(lp.compareLevels(mp) < 0, l);
        if (rc == 0 && !advanced) {
            return get(r, null, (n, a, b) -> rp.op(mp.op(al, a), b));
        } else {
            return mp.op(al, NodeRenderer.paren(rc == 0 && advanced || rc < 0, r));
        }
    }

    private boolean hasOther(final Node<C> node, final Priority priority) {
        return get(node, () -> false, (name, l, r) -> {
            final Priority p = Functional.get(priorities, name);
            if (p.compareLevels(priority) != 0) {
                return false;
            }
            return p != priority || hasOther(l, priority);
        });
    }

    private Node<C> op(final Priority mp, final Node<C> l, final Node<C> r) {
        return mp.op(l, r);
    }

    private Priority priority(final Node<C> node) {
        return get(node, () -> Priority.MAX, (n, a, b) -> Functional.get(priorities, n));
    }

    private <R> R get(final Node<C> node, final Supplier<R> common, final Node.Binary<Node<C>, R> binary) {
        return node.get(
                c -> common.get(),
                n -> common.get(),
                (n, p, a) -> common.get(),
                binary
        );
    }

    public NodeRenderer<C> build() {
        return new NodeRenderer<>(nodeRenderer.build(), brackets, random);
    }

    // :NOTE: Especially ugly bit-fiddling, do not replicate
    private record Priority(String op, int priority) {
        private static final int Q = 3;
        private static final Priority MAX = new Priority("MAX", Integer.MAX_VALUE - Q);

        private int compareLevels(final Priority that) {
            return (priority | Q) - (that.priority | Q);
        }

        @Override
        public String toString() {
            return String.format("Priority(%s, %d, %d)", op, priority | Q, priority & Q);
        }

        public <C> Node<C> op(final Node<C> l, final Node<C> r) {
            return Node.op(op, l, r);
        }

        private boolean has(final int value) {
            return (priority & Q) == value;
        }
    }
}

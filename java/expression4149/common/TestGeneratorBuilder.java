package expression.common;

import base.ExtendedRandom;
import base.Functional;
import expression.ToMiniString;
import expression.common.ExpressionKind.Variables;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class TestGeneratorBuilder<C> {
    private final ExtendedRandom random;

    private final Generator.Builder<C> generator;
    private final NodeRendererBuilder<C> renderer;

    private final List<Function<List<Node<C>>, Stream<Node<C>>>> basicTests = new ArrayList<>();
    private final List<Node<C>> consts;
    private final boolean verbose;

    public TestGeneratorBuilder(
            final ExtendedRandom random,
            final Supplier<C> constant,
            final List<C> constants,
            final boolean verbose
    ) {
        this.random = random;
        this.verbose = verbose;

        generator = Generator.builder(constant, random);
        renderer = new NodeRendererBuilder<>(random);

        consts = Functional.map(constants, Node::constant);
        basicTests.add(vars -> consts.stream());
        basicTests.add(List::stream);
    }

    private Node<C> c() {
        return random.randomItem(consts);
    }

    private Node<C> v(final List<Node<C>> variables) {
        return random.randomItem(variables);
    }

    private static <C> Node<C> f(final String name, final int priority, final Node<C> arg) {
        return Node.op(name, priority, arg);
    }

    private static <C> Node<C> f(final String left, final Node<C> arg) {
        return Node.op(left, Integer.MAX_VALUE, arg);
    }

    private static <C> Node<C> f(final String name, final Node<C> arg1, final Node<C> arg2) {
        return Node.op(name, arg1, arg2);
    }

    @SafeVarargs
    private void basicTests(final Function<List<Node<C>>, Node<C>>... tests) {
        Arrays.stream(tests).map(test -> test.andThen(Stream::of)).forEachOrdered(basicTests::add);
    }

    public void unary(final String name, final int priority) {
        generator.add(name, (priority & 1) * 2 - 1);
        renderer.unary(name, priority);

        if (verbose) {
            basicTests.add(vars -> Stream.concat(consts.stream(), vars.stream()).map(a -> f(name, priority, a)));
        } else {
            basicTests(vars -> f(name, priority, c()), vars -> f(name, priority, v(vars)));
        }

        final Function<List<Node<C>>, Node<C>> p1 = vars -> f(name, priority, f(name, priority, f("+", v(vars), c())));
        final Function<List<Node<C>>, Node<C>> p2 = vars -> f("*", v(vars), f("*", v(vars), f(name, priority, c())));
        basicTests(
                vars -> f(name, priority, f("+", v(vars), v(vars))),
                vars -> f(name, priority, f(name, priority, v(vars))),
                vars -> f(name, priority, f("/", f(name, priority, v(vars)), f("+", v(vars), v(vars)))),
                p1,
                p2,
                vars -> f("+", p1.apply(vars), p2.apply(vars))
        );
    }

    public void unary(final String left, final String right) {
        generator.add(left, 1);
        renderer.unary(left, right);

        if (verbose) {
            basicTests.add(vars -> Stream.concat(consts.stream(), vars.stream()).map(a -> f(left, a)));
        } else {
            basicTests(vars -> f(left, c()), vars -> f(left, v(vars)));
        }

        final Function<List<Node<C>>, Node<C>> p1 = vars -> f(left, f(left, f("+", v(vars), c())));
        final Function<List<Node<C>>, Node<C>> p2 = vars -> f("*", v(vars), f("*", v(vars), f(left, c())));
        basicTests(
                vars -> f(left, f("+", v(vars), v(vars))),
                vars -> f(left, f(left, v(vars))),
                vars -> f(left, f("/", f(left, v(vars)), f("+", v(vars), v(vars)))),
                p1,
                p2,
                vars -> f("+", p1.apply(vars), p2.apply(vars))
        );
    }

    public void binary(final String name, final int priority) {
        generator.add(name, 2);
        renderer.binary(name, priority);

        if (verbose) {
            basicTests.add(vars -> Stream.concat(consts.stream(), vars.stream().limit(3))
                            .flatMap(a -> consts.stream().map(b -> f(name, a, b))));
        } else {
            basicTests(
                    vars -> f(name, c(), c()),
                    vars -> f(name, v(vars), c()),
                    vars -> f(name, c(), v(vars)),
                    vars -> f(name, v(vars), v(vars))
            );
        }

        final Function<List<Node<C>>, Node<C>> p1 = vars -> f(name, f(name, f("+", v(vars), c()), v(vars)), v(vars));
        final Function<List<Node<C>>, Node<C>> p2 = vars -> f("*", v(vars), f("*", v(vars), f(name, c(), v(vars))));

        basicTests(
                vars -> f(name, f(name, v(vars), v(vars)), v(vars)),
                vars -> f(name, v(vars), f(name, v(vars), v(vars))),
                vars -> f(name, f(name, v(vars), v(vars)), f(name, v(vars), v(vars))),
                vars -> f(name, f("-", f(name, v(vars), v(vars)), c()), f("+", v(vars), v(vars))),
                p1,
                p2,
                vars -> f("+", p1.apply(vars), p2.apply(vars))
        );
    }

    public <E extends ToMiniString> TestGenerator<C,E> build(final Variables<E> variables) {
        return new TestGenerator<>(generator.build(variables, basicTests), renderer.build());
    }
}

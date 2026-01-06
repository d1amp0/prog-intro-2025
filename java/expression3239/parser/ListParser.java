package expression.parser;

import expression.ListExpression;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface ListParser {
    ListExpression parse(String expression, List<String> variables);
}

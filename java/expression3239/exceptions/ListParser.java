package expression.exceptions;

import expression.ListExpression;

import java.util.List;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
@FunctionalInterface
public interface ListParser {
    ListExpression parse(String expression, final List<String> variables) throws Exception;
}

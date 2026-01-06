package expression.generic;

import expression.exceptions.ParserException;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2)
            throws ParserException {
        GenericOperations<?> operations = switch (mode) {
            case "i" -> new IntegerCheckOperations();
            case "d" -> new DoubleOperations();
            case "bi" -> new BigIntegerOperations();
            case "u" -> new IntegerNoCheckOperations();
            case "s" -> new ShortOperations();
            case "f" -> new FloatOperations();
            default -> throw new IllegalArgumentException("Not a valid type: " + mode);
        };
        return table(operations, expression, x1, x2, y1, y2, z1, z2);
    }

    public <T extends Number> Object[][][] table(GenericOperations<T> operations, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws ParserException {
        GenericExpressionParser<T> parser = new GenericExpressionParser<>(operations);
        GenericTripleExpression<T> exp = parser.parse(expression);
        int xSize = x2 - x1 + 1, ySize = y2 - y1 + 1, zSize = z2 - z1 + 1;
        Object[][][] answer = new Object[xSize][ySize][zSize];
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    T first = operations.valueOf(x + x1);
                    T second = operations.valueOf(y + y1);
                    T third = operations.valueOf(z + z1);
                    try {
                        answer[x][y][z] = exp.evaluate(first, second, third);
                    } catch (Exception e) {
                        answer[x][y][z] = null;
                    }
                }
            }
        }
        return answer;
    }
}

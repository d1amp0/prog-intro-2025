package expression.parser;
import expression.*;

import java.util.function.Function;

public class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(String string) {
        return new ExpressionParserI(new StringSource(string)).parse();
    }

    private static class ExpressionParserI extends BaseParser {

        public ExpressionParserI(CharSource source) {
            super(source);
        }

        public TripleExpression parse() {
            TripleExpression result = parseOr();
            skipWS();
            if (!checkEOF()) {
                throw new IllegalArgumentException("Expected EOF, but found:" + take());
            }
            return result;
        }

        private AnyExpression parseOr() {
            AnyExpression result = parseXor();
            while (true) {
                skipWS();
                if (take('|')) {
                    result = new Or(result, parseXor());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseXor() {
            AnyExpression result = parseAnd();
            while (true) {
                skipWS();
                if (take('^')) {
                    result = new Xor(result, parseAnd());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseAnd() {
            AnyExpression result = parseOld();
            while (true) {
                skipWS();
                if (take('&')) {
                    result = new And(result, parseOld());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseOld() {
            AnyExpression result = parseMid();
            while (true) {
                skipWS();
                if (take('+')) {
                    result = new Add(result, parseMid());
                } else if (take('-')) {
                    result = new Subtract(result, parseMid());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseMid() {
            AnyExpression result = parseYoung();
            while (true) {
                skipWS();
                if (take('*')) {
                    result = new Multiply(result, parseYoung());
                } else if (take('/')) {
                    result = new Divide(result, parseYoung());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseYoung() {
            skipWS();
            if (take('-')) {
                if (between('0', '9')) {
                    return parseNumber(true);
                } else {
                    return new Negate(parseYoung());
                }
            } else if (take('(')) {
                AnyExpression result = parseOr();
                skipWS();
                expect(')');
                return result;
            } else if (between('0', '9')) {
                return parseNumber(false);
            } else if (test('x') || test('y') || test('z')) {
                return parseVar();
            }
            throw new IllegalArgumentException("Incorrect variable: " + take());
        }

        private AnyExpression parseVar() {
            if (take('x')) {
                return new Variable("x");
            } else if (take('y')) {
                return new Variable("y");
            } else if (take('z')) {
                return new Variable("z");
            }
            throw new IllegalArgumentException("Incorrect variable");
        }

        private AnyExpression parseNumber(boolean isMinus) {
            StringBuilder sb = new StringBuilder();
            if (isMinus) {
                sb.append('-');
            }
            if (take('0')) {
                return new Const(0);
            }
            while (between('0', '9')) {
                sb.append(take());
            }
            try {
                return new Const(Integer.parseInt(sb.toString()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Incorrect number");
            }
        }
    }
}

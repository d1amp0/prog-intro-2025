package expression.exceptions;
import expression.*;
import expression.parser.StringSource;
import expression.parser.BaseParser;
import expression.parser.CharSource;

public class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(String string) throws ParserException {
        return new ExpressionParserI(new StringSource(string)).parse();
    }

    private static class ExpressionParserI extends BaseParser {

        public ExpressionParserI(CharSource source) {
            super(source);
        }

        public TripleExpression parse() throws ParserException {
            TripleExpression result = parseOldest();
            skipWS();
            if (!checkEOF()) {
                throw new ParserEOFException(take(), getPosition());
            }
            return result;
        }

        private AnyExpression parseOldest() throws ParserException {
            AnyExpression result = parseOld();
            while (true) {
                skipWS();
                if (take("gcd")) {
                    if (between('0', '9') || test('x') || test('y') || test('z')) {
                        throw new ParserNoSpaceException(take(), getPosition());
                    }
                    result = new CheckedGcd(result, parseOld());
                } else if (take("lcm")) {
                    if (between('0', '9') || test('x') || test('y') || test('z')) {
                        throw new ParserNoSpaceException(take(), getPosition());
                    }
                    result = new CheckedLcm(result, parseOld());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseOld() throws ParserException {
            AnyExpression result = parseMid();
            while (true) {
                skipWS();
                if (take('+')) {
                    result = new CheckedAdd(result, parseMid());
                } else if (take('-')) {
                    result = new CheckedSubtract(result, parseMid());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseMid() throws ParserException {
            AnyExpression result = parseYoung();
            while (true) {
                skipWS();
                if (take('*')) {
                    result = new CheckedMultiply(result, parseYoung());
                } else if (take('/')) {
                    result = new CheckedDivide(result, parseYoung());
                } else {
                    return result;
                }
            }
        }

        private AnyExpression parseYoung() throws ParserException {
            skipWS();
            if (take('-')) {
                if (between('0', '9')) {
                    return parseNumber(true);
                } else {
                    return new CheckedNegate(parseYoung());
                }
            } else if (take('(')) {
                AnyExpression result = parseOldest();
                skipWS();
                try {
                    expect(')');
                } catch (IllegalArgumentException e) {
                    throw new ParserParenthesisException(take(), getPosition());
                }
                return result;
            } else if (between('0', '9')) {
                return parseNumber(false);
            } else if (test('x') || test('y') || test('z')) {
                return parseVar();
            }
            throw new ParserWrongNumberAndVariableException(take(), getPosition());
        }

        private AnyExpression parseVar() {
            if (take('x')) {
                return new Variable("x");
            } else if (take('y')) {
                return new Variable("y");
            }
            take('z');
            return new Variable("z");
        }

        private AnyExpression parseNumber(boolean isMinus) throws ParserException {
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
                throw new ParserOverflowException(sb.toString(), getPosition());
            }
        }
    }
}

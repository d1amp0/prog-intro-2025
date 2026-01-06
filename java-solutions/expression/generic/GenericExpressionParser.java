package expression.generic;

import expression.parser.CharSource;
import expression.parser.StringSource;
import expression.parser.BaseParser;
import expression.exceptions.*;

public class GenericExpressionParser<T extends Number> {
    private final GenericOperations<T> calculator;

    public GenericExpressionParser(GenericOperations<T> calculator) {
        this.calculator = calculator;
    }

    public GenericTripleExpression<T> parse(String expression) throws ParserException {
        return new Parser<>(new StringSource(expression), calculator).parse();
    }

    private static class Parser<T extends Number> extends BaseParser {
        private final GenericOperations<T> calculator;

        public Parser(CharSource source, GenericOperations<T> calculator) {
            super(source);
            this.calculator = calculator;
        }

        public GenericTripleExpression<T> parse() throws ParserException {
            GenericTripleExpression<T> result = parseOldest();
            skipWS();
            if (!checkEOF()) {
                throw new ParserEOFException(take(), getPosition());
            }
            return result;
        }

        private GenericTripleExpression<T> parseOldest() throws ParserException {
            GenericTripleExpression<T> result = parseOld();
            while (true) {
                skipWS();
                if (take("min")) {
                    if (between('0', '9') || test('x') || test('y') || test('z')) {
                        throw new ParserNoSpaceException(take(), getPosition());
                    }
                    result = new GenericMin<>(result, parseOld(), calculator);
                } else if (take("max")) {
                    if (between('0', '9') || test('x') || test('y') || test('z')) {
                        throw new ParserNoSpaceException(take(), getPosition());
                    }
                    result = new GenericMax<>(result, parseOld(), calculator);
                } else {
                    return result;
                }
            }
        }

        private GenericTripleExpression<T> parseOld() throws ParserException {
            GenericTripleExpression<T> result = parseMid();
            while (true) {
                skipWS();
                if (take('+')) {
                    result = new GenericAdd<>(result, parseMid(), calculator);
                } else if (take('-')) {
                    result = new GenericSubtract<>(result, parseMid(), calculator);
                } else {
                    break;
                }
            }
            return result;
        }

        private GenericTripleExpression<T> parseMid() throws ParserException {
            GenericTripleExpression<T> result = parseYoung();
            while (true) {
                skipWS();
                if (take('*')) {
                    result = new GenericMultiply<>(result, parseYoung(), calculator);
                } else if (take('/')) {
                    result = new GenericDivide<>(result, parseYoung(), calculator);
                } else {
                    break;
                }
            }
            return result;
        }

        private GenericTripleExpression<T> parseYoung() throws ParserException {
            skipWS();
            if (take('-')) {
                if (between('0', '9')) {
                    return parseNumber(true);
                } else {
                    return new GenericNegate<>(parseYoung(), calculator);
                }
            } else if (take('(')) {
                GenericTripleExpression<T> result = parseOldest();
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
            } else if (take("count")) {
                return new GenericCount<>(parseYoung(), calculator);
            }
            throw new ParserWrongNumberAndVariableException(take(), getPosition());
        }

        private GenericTripleExpression<T> parseVar() {
            if (take('x')) {
                return new GenericVariable<>("x");
            } else if (take('y')) {
                return new GenericVariable<>("y");
            }
            take('z');
            return new GenericVariable<>("z");
        }

        private GenericTripleExpression<T> parseNumber(boolean isMinus) throws ParserException {
            StringBuilder sb = new StringBuilder();
            if (isMinus) {
                sb.append('-');
            }
            if (take('0')) {
                return new GenericConst<>(this.calculator.parseConst("0"));
            }
            while (between('0', '9')) {
                sb.append(take());
            }
            try {
                return new GenericConst<>(this.calculator.parseConst(sb.toString()));
            } catch (NumberFormatException e) {
                throw new ParserOverflowException(sb.toString(), getPosition());
            }
        }
    }
}
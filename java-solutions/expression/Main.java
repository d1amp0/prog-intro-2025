//package expression;
//import expression.exceptions.ExpressionException;
//import expression.exceptions.ExpressionParser;
//import expression.exceptions.ParserException;
//
//public class Main {
//    public static void main(String[] args) {
//        ExpressionParser expressionParser = new ExpressionParser();
//        try {
//            TripleExpression exp = expressionParser.parse("1000000*x*x*x*x*x/(x-1)");
//            System.out.println(" x   f");
//            for (int i = 0; i < 11; i++) {
//                try {
//                    System.out.println(" " + i + "   " + exp.evaluate(i, 0, 0));
//                } catch (ExpressionException e) {
//                    System.out.println(" " + i + "   " + e.getMessage());
//                }
//            }
//        } catch (ParserException e) {
//             Кажется, что так плохо делать, но так требуется в задании
//            System.out.println(e.getMessage());
//        }
//    }
//}
package expression;
import expression.exceptions.ParserException;
import expression.generic.GenericTabulator;

public class Main {
    public static void main(String[] args) {
        GenericTabulator genericTabulator = new GenericTabulator();
        String mode = args[0].substring(1);
        String expression = args[1];
        try {
            Object[][][] answer = genericTabulator.tabulate(mode, expression,
                    -2, 2, -2, 2, -2, 2);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    for (int k = 0; k < 5; k++) {
                        System.out.printf("Cell[%d][%d][%d] is %s", i, j, k, answer[i][j][k]);
                        System.out.println();
                    }
                }
            }
        } catch (ParserException e) {
            System.out.println(e.getMessage());
        }
    }
}
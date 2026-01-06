import java.util.Arrays;
import java.io.IOException;

public class ReverseAvg {
    public static void main(String[] args) {
        int[][] matrix = new int[4][];
        int linesCount = 0, maxLineLength = 0;
        try {
            FastScanner systemScanner = new FastScanner(System.in);
            try {
                while (systemScanner.hasNextLine()) {
                    if (linesCount == matrix.length) {
                        matrix = Arrays.copyOf(matrix, 2 * linesCount);
                    }
                    String line = systemScanner.nextLine();
                    if (!line.isEmpty()) {
                        try {
                            FastScanner lineScanner = new FastScanner(line, false);
                            try {
                                matrix[linesCount] = new int[4];
                                int intCount = 0;
                                while (lineScanner.hasNextInt()) {
                                    if (intCount == matrix[linesCount].length) {
                                        matrix[linesCount] = Arrays.copyOf(matrix[linesCount], 2 * intCount);
                                    }
                                    matrix[linesCount][intCount] = lineScanner.nextInt();
                                    intCount += 1;
                                }
                                matrix[linesCount] = Arrays.copyOf(matrix[linesCount], intCount);
                                if (intCount > maxLineLength) {
                                    maxLineLength = intCount;
                                }
                            } finally {
                                lineScanner.close();
                            }
                        } catch (IOException e) {
                            System.err.println("IO error : " + e.getMessage());
                            matrix[linesCount] = new int[0];
                        }
                    } else {
                        matrix[linesCount] = new int[0];
                    }
                    linesCount++;
                }
                matrix = Arrays.copyOf(matrix, linesCount);
            } finally {
                systemScanner.close();
            }
            matrix = Arrays.copyOf(matrix, linesCount);
            systemScanner.close();
            long[][] rowSum = new long[2][linesCount];
            long[][] colSum = new long[2][maxLineLength];
            for (int i = 0; i < linesCount; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    rowSum[0][i] += matrix[i][j];
                    rowSum[1][i] += 1;
                    colSum[0][j] += matrix[i][j];
                    colSum[1][j] += 1;
                }
            }
            for (int i = 0; i < linesCount; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    System.out.print((rowSum[0][i] + colSum[0][j] - matrix[i][j]) / (rowSum[1][i] + colSum[1][j] - 1) + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        }
    }
}


/*
import java.util.Arrays;
import java.util.Scanner;


public class ReverseAvg {
    public static int DEFAULT_ARRAY_LENGTH = 4;
    public static void main(String[] args) {
        FastScanner systemScanner = new FastScanner(System.in);
        int[][] matrix = new int[DEFAULT_ARRAY_LENGTH][];
        int linesCount = 0, maxLineLength = 0;
        while (systemScanner.hasNextLine()) {
            if (linesCount == matrix.length) {
                matrix = Arrays.copyOf(matrix, 2 * linesCount);
            }
            String line = systemScanner.nextLine();
            if (!line.isBlank()) {
                FastScanner lineScanner = new FastScanner(line, false);
                matrix[linesCount] = new int[4];
                int intCount = 0;
                while (lineScanner.hasNextInt()) {
                    if (intCount == matrix[linesCount].length) {
                        matrix[linesCount] = Arrays.copyOf(matrix[linesCount], 2 * intCount);
                    }
                    matrix[linesCount][intCount] = lineScanner.nextInt();
                    intCount += 1;
                }
                matrix[linesCount] = Arrays.copyOf(matrix[linesCount], intCount);
                if (intCount > maxLineLength) {
                    maxLineLength = intCount;
                }
            } else {
                matrix[linesCount] = new int[0];
            }
            linesCount++;
        }
        matrix = Arrays.copyOf(matrix, linesCount);
        systemScanner.close();
        long[][] rowSum = new long[2][linesCount];
        long[][] colSum = new long[2][maxLineLength];
        for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                rowSum[0][i] += matrix[i][j];
                rowSum[1][i] += 1;
                colSum[0][j] += matrix[i][j];
                colSum[1][j] += 1;
            }
        }
        for (int i = 0; i < linesCount; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print((rowSum[0][i] + colSum[0][j] - matrix[i][j]) / (rowSum[1][i] + colSum[1][j] - 1) + " ");
            }
            System.out.println();
        }
    }
}
*/
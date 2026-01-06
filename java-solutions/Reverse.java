import java.util.Arrays;
import java.io.IOException;

public class Reverse {
    public static void main(String[] args) {
        int[][] matrix = new int[4][];
        int linesCount = 0;
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
            for (int i = linesCount - 1; i >= 0; i--) {
                for (int j = matrix[i].length - 1; j >= 0; j--) {
                    System.out.print(matrix[i][j] + " ");
                }
                System.out.println();
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        }
    }
}
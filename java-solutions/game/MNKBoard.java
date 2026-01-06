package game;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public class MNKBoard implements Board {
    private final Cell[][] field;
    private Cell turn;
    private final int m;
    private final int n;
    private final int k;
    private final int lengthM;
    private final int lengthN;
    private int emptyCells;
    private final boolean isRomb;

    private Map<Cell, String> cellStringMap = new HashMap<>();

    public MNKBoard(int m, int n, int k, boolean isRomb) {
        cellStringMap.put(Cell.X, "X");
        cellStringMap.put(Cell.O, "O");
        cellStringMap.put(Cell.E, " ");
        cellStringMap.put(Cell.C, "=");
        if (isRomb) {
            this.m = m + n - 1;
            this.n = m + n - 1;
        } else {
            this.m = m;
            this.n = n;
        }
        this.k = k;
        this.isRomb = isRomb;
        this.emptyCells = this.m * this.n;
        lengthM = String.valueOf(this.m).length();
        lengthN = String.valueOf(this.n).length() + 1;
        field = new Cell[this.m][this.n];
        if (isRomb) {
            int i = m - 1, j = n - 1, dI = -1, dJ = -1;
            for (Cell[] row : field) {
                Arrays.fill(row, 0, i, Cell.C);
                emptyCells -= i;
                Arrays.fill(row, i, i + m + n - 1 - i - j, Cell.E);
                Arrays.fill(row, m + n - 1 - j, this.m, Cell.C);
                emptyCells -= j;
                i += dI;
                j += dJ;
                if (i == -1) {
                    i = 1;
                    dI = 1;
                }
                if (j == -1) {
                    j = 1;
                    dJ = 1;
                }
            }
        } else {
            for (Cell[] row : field) {
                Arrays.fill(row, Cell.E);
            }
        }
        turn = Cell.X;
    }

    @Override
    public void clear() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (field[i][j] == Cell.X || field[i][j] == Cell.O) {
                    field[i][j] = Cell.E;
                    emptyCells++;
                }
            }
        }
        turn = Cell.X;
    }

    private class ImutablePosition implements Position {
        @Override
        public Cell getTurn() {
            return turn;
        }

        @Override
        public boolean isValid(Move move) {
            return 0 <= move.getCol() && move.getCol() < n
                    && 0 <= move.getRow() && move.getRow() < m
                    && field[move.getRow()][move.getCol()] == Cell.E
                    && move.getCell() == turn;
        }

        @Override
        public String toString() {
            return MNKBoard.this.toString();
        }
    }

    @Override
    public void setCells(char xSymbol, char oSymbol) {
        cellStringMap.put(Cell.X, String.valueOf(xSymbol));
        cellStringMap.put(Cell.O, String.valueOf(oSymbol));
    }

    private Result checkDirection(int row, int col, int dRow, int dCol) {
        // NOTE: есть дублирование кода
        int sameCellsCount = 1;
        int i = row + dRow, j = col + dCol;
        while (i >= 0 && i < m && j >= 0 && j < n && field[i][j] == turn) {
            sameCellsCount++;
            i += dRow;
            j += dCol;
        }
        i = row - dRow;
        j = col - dCol;
        while (i >= 0 && i < m && j >= 0 && j < n && field[i][j] == turn) {
            sameCellsCount++;
            i -= dRow;
            j -= dCol;
        }
        return sameCellsCount >= k ? Result.WIN : Result.UNKNOWN;
    }
    
    private Result checkField(int row, int col) {
        // NOTE: такие константы не нужно объявлять внутри методов
        final int[] dR = {1, 0, 1, 1};
        final int[] dC = {0, 1, 1, -1};
        Result result;
        for (int i = 0; i < 4; i++) {
            result = checkDirection(row, col, dR[i], dC[i]);
            if (result != Result.UNKNOWN) {
                return result;
            }
        }
        if (emptyCells == 0) {
            return Result.DRAW;
        }
        return Result.UNKNOWN;
    }

    @Override
    public Position getPosition() {
        return new ImutablePosition();
    }

    @Override
    public Result makeMove(Move move, boolean isHumanPlayer) {
        if (!isValid(move)) {
            if (isHumanPlayer) {
                return Result.WRONG_INPUT;
            }
            return Result.LOSE;
        }
        field[move.getRow()][move.getCol()] = move.getCell();
        emptyCells--;
        Result preliminaryResult = checkField(move.getRow(), move.getCol());
        if (preliminaryResult != Result.UNKNOWN) {
            return preliminaryResult;
        }
        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;
    }

    public boolean isValid(Move move) {
        return 0 <= move.getCol() && move.getCol() < n
                && 0 <= move.getRow() && move.getRow() < m
                && field[move.getRow()][move.getCol()] == Cell.E
                && move.getCell() == turn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" ".repeat(lengthM + 1));
        for (int i = 0; i < n; i++) {
            sb.append(i + 1).append(" ".repeat(lengthN - String.valueOf(i + 1).length()));
        }
        sb.append("\n").append(" ".repeat(lengthM)).append("+").append("-".repeat(lengthN * n)).append("\n");
        int lengthM = String.valueOf(m).length();
        for (int r = 0; r < m; r++) {
            sb.append(r + 1).append(" ".repeat(lengthM - String.valueOf(r + 1).length())).append("|");
            for (int c = 0; c < n; c++) {
                sb.append(cellStringMap.get(field[r][c])).append(" ".repeat(lengthN - 1));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

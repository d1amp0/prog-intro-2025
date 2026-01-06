package game;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private final Scanner in;
    private final PrintStream out;

    public HumanPlayer(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public HumanPlayer(char selfSymbol) {
        this(new Scanner(System.in), System.out);
        this.selfSymbol = selfSymbol;
    }

    @Override
    public Move makeMove(Position position) {
        out.println("Your turn " + selfSymbol);

        out.println("Current position:\n" + position);
        int row, col;
        while (true) {
            try {
                row = in.nextInt() - 1;
                col = in.nextInt() - 1;
                break;
            } catch (InputMismatchException e) {
                out.println("Symbol is not a number for player " + position.getTurn() + ". Try again");
                System.err.println("Wrong number: " + e.getMessage());
                in.nextLine();
            }
        }
        return new Move(row, col, position.getTurn());
    }
}

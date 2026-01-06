package game;

public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private final boolean log;

    public Game(Board board, Player player1, Player player2, boolean log) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.log = log;
    }

    public int play() {
        board.setCells(player1.getSelfSymbol(), player2.getSelfSymbol());
        while (true) {
            int result1 = getResult(1, player1);
            if (result1 != -1) {
                return result1;
            }
            int result2 = getResult(2, player2);
            if (result2 != -1) {
                return result2;
            }
        }
    }

    private int getResult(int playerNumber, Player player) {
        Move move;
        try {
            move = player.makeMove(board.getPosition());
        } catch (RuntimeException e) {
            return 3 - playerNumber;
        }
        if (move == null) {
            return 3 - playerNumber;
        }
        if (log) {
            System.out.println("Player #" + playerNumber + " move: " + move);
            System.out.println(board);
        }
        Result result = board.makeMove(move, player instanceof HumanPlayer);
        if (result == Result.WRONG_INPUT) {
            System.out.println("Wrong input for player " + board.getPosition().getTurn() + ". Try again");
            return getResult(playerNumber, player);
        }
        if (result == Result.WIN) {
            return playerNumber;
        } else if (result == Result.LOSE) {
            return 3 - playerNumber;
        } else if (result == Result.DRAW) {
            return 0;
        }
        return -1;
    }
}

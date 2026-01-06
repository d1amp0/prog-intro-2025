package game;

import java.util.Random;

public class RandomPlayer extends Player {
    private Random random = new Random();

    public RandomPlayer(char selfSymbol) {
        this.selfSymbol = selfSymbol;
    }

    @Override
    public Move makeMove(Position position) {
        while (true) {
            Move move = new Move(
                    random.nextInt(100),
                    random.nextInt(100),
                    position.getTurn());
            if (position.isValid(move)) {
                return move;
            }
        }
    }
}

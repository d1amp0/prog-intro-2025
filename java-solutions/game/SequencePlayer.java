package game;

public class SequencePlayer extends Player {
    public SequencePlayer(char selfSymbol) {
        this.selfSymbol = selfSymbol;
    }

    @Override
    public Move makeMove(Position position) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Move move = new Move(
                        r,
                        c,
                        position.getTurn());
                if (position.isValid(move)) {
                    return move;
                }
            }
        }
        return null;
    }
}

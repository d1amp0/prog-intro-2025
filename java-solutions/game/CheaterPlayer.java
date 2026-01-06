package game;

public class CheaterPlayer extends Player {
    public CheaterPlayer(char selfSymbol) {
        this.selfSymbol = selfSymbol;
    }

    @Override
    public Move makeMove(Position position) {
        return null;
//        return new Move(3, 3, position.getTurn());
    }
}

package game;

public abstract class Player {
    protected char selfSymbol;

    public char getSelfSymbol() {
        return selfSymbol;
    }
    public abstract Move makeMove(Position position);
}

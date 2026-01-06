package game;

public interface Board {
    Position getPosition();
    Result makeMove(Move move, boolean isHumanPlayer);
    void setCells(char xCell, char oSell);
    void clear();
}

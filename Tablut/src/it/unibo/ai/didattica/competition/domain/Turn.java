package it.unibo.ai.didattica.competition.domain;

/**
 * Turn represent the player that has to move or the end of the game(A win
 * by a player or a draw)
 *
 * @author A.Piretti
 */
public enum Turn {
    WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
    private final String turn;

    private Turn(String s) {
        turn = s;
    }

    public boolean equalsTurn(String otherName) {
        return (otherName == null) ? false : turn.equals(otherName);
    }

    public String toString() {
        return turn;
    }
}

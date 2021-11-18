package it.unibo.ai.didattica.competition.domain;

/**
 * Pawn represents the content of a box in the board
 *
 * @author A.Piretti
 */
public enum Pawn {
    EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
    private final String pawn;

    private Pawn(String s) {
        pawn = s;
    }

    public boolean equalsPawn(String otherPawn) {
        return (otherPawn == null) ? false : pawn.equals(otherPawn);
    }

    public String toString() {
        return pawn;
    }

}

package it.unibo.ai.didattica.competition.domain;

public interface IState {

    public Pawn[][] getBoard();

    public String boardString();

    public Pawn getPawn(int row, int column);

    public void removePawn(int row, int column);

    public void setBoard(Pawn[][] board);

    public Turn getTurn();

    public void setTurn(Turn turn);

    public String getBox(int row, int column);

    public IState clone();

    public int getNumberOf(Pawn color);

}

package it.unibo.ai.didattica.competition.tablut.ai.model;

import it.unibo.ai.didattica.competition.tablut.ai.utility.Pair;
import it.unibo.ai.didattica.competition.tablut.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class StateDecorator implements IState {

    private final IState state;
    private final List<Coordinate> camps;
    private final Direction[] directions;
    public StateDecorator(IState state){
        this.state = state;
        camps = TablutUtility.getInstance().getCamps();
        directions = new Direction[]{Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
    }

    //capisci se è corretto
    public List<Pair<Coordinate,Pawn>> LookDirection(Direction dir, Coordinate p) {
        List<Pair<Coordinate,Pawn>> pawns = new ArrayList<>();
        TablutUtility.getInstance().switchOnT(dir, p,
                cord -> {
                    for (int i = cord.getRow(); i > 0; i--) {
                        Pair<Coordinate, Pawn> pair =
                                new Pair<>(new Coordinate(i, cord.getCol()),state.getPawn(i, cord.getCol()));
                        pawns.add(pair);
                    }
                },
                cord -> { for (int i = cord.getRow(); i < state.getBoard().length; i++) {
                    Pair<Coordinate, Pawn> pair =
                            new Pair<>(new Coordinate(i, cord.getCol()),state.getPawn(i, cord.getCol()));
                    pawns.add(pair);
                }},
                cord -> { for (int j = cord.getCol(); j > 0; j--) {
                    Pair<Coordinate, Pawn> pair =
                            new Pair<>(new Coordinate(cord.getRow(), j),state.getPawn(cord.getRow(), j));
                    pawns.add(pair);
                }},
                cord -> {for (int j = cord.getCol(); j < state.getBoard()[cord.getRow()].length; j++) {
                    Pair<Coordinate, Pawn> pair =
                            new Pair<>(new Coordinate(cord.getRow(), j),state.getPawn(cord.getRow(), j));
                    pawns.add(pair);
                }}
                );

        return pawns;
    }
    public List<Action> getAllWhiteMoves(){
        Map<Pawn, List<Coordinate>> pieces = this.getPieces();
        List<Coordinate> whitePieces = pieces.get(Pawn.WHITE);
        whitePieces.add(pieces.get(Pawn.KING).get(0));
        List<Action> moves = new ArrayList<>();
        for(Coordinate corWhitePos : whitePieces) {
            for (Direction dir : directions){
               List<Pair<Coordinate, Pawn>> candidateMoves = LookDirection(dir, corWhitePos);
               fillMoves(candidateMoves, corWhitePos,
                       (pair) -> pair.getSecond() == Pawn.EMPTY && !camps.contains(pair.getFirst().getBox())
                       ,moves);
           }
        }
        return moves;
    }

    public List<Action> getAllBlackMoves(){
        Map<Pawn, List<Coordinate>> pieces = this.getPieces();
        List<Coordinate> blackPieces = pieces.get(Pawn.BLACK);
        List<Action> moves = new ArrayList<>();
        for(Coordinate corBlackPos : blackPieces) {
            boolean insideCamp = camps.contains(corBlackPos);
            for (Direction dir : directions) {
                List<Pair<Coordinate, Pawn>> candidateMoves = LookDirection(dir, corBlackPos);
                fillMoves(candidateMoves, corBlackPos,
                        (pair) -> pair.getSecond() == Pawn.EMPTY && (!camps.contains(pair.getFirst()) || insideCamp)
                        ,moves);
            }
        }
        return moves;
    }

    private void fillMoves(List<Pair<Coordinate, Pawn>> candidateMoves,
                           Coordinate corPos,
                           Predicate<Pair<Coordinate, Pawn>> pred,
                           List<Action> moves) {
        candidateMoves.stream()
                .takeWhile(pred)
                .forEach(pair -> {
                    String from = corPos.getBox();
                    String to = pair.getFirst().getBox();
                    try {
                        moves.add(new Action(from, to, Turn.BLACK));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }



    /**
     * gives all the pieces of the board classified separately in Black, White and King.
     */
    public Map<Pawn, List<Coordinate>> getPieces(){
        Pawn[][] board = state.getBoard();
        Map<Pawn, List<Coordinate>> map = new HashMap<>();
        map.put(Pawn.KING, new ArrayList<>());
        map.put(Pawn.WHITE, new ArrayList<>());
        map.put(Pawn.BLACK, new ArrayList<>());
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (map.containsKey(board[i][j]))
                    map.get(board[i][j]).add(new Coordinate(i, j));
            }
        }
        return map;
    }


    public IState getState() {
        return state;
    }

    @Override
    public Pawn[][] getBoard() {
        return this.state.getBoard();
    }

    @Override
    public String boardString() {
        return this.state.boardString();
    }

    @Override
    public Pawn getPawn(int row, int column) {
        return this.state.getPawn(row, column);
    }

    @Override
    public void removePawn(int row, int column) {
        this.state.removePawn(row, column);
    }

    @Override
    public void setBoard(Pawn[][] board) {
        this.state.setBoard(board);
    }

    @Override
    public Turn getTurn() {
        return this.state.getTurn();
    }

    @Override
    public void setTurn(Turn turn) {
        this.state.setTurn(turn);
    }

    @Override
    public String getBox(int row, int column) {
       return this.state.getBox(row, column);
    }

    @Override
    public IState clone() {
        return this.state.clone();
    }

    @Override
    public int getNumberOf(Pawn color){
        return this.state.getNumberOf(color);
    }

}

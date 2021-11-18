package it.unibo.ai.didattica.competition.ai.utility;

import it.unibo.ai.didattica.competition.ai.model.Coordinate;
import it.unibo.ai.didattica.competition.ai.model.Direction;
import it.unibo.ai.didattica.competition.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TablutUtility {

    private static TablutUtility instance = null;
    private GameAshtonTablut gameAshtonTablut;
    private final List<Coordinate> winningPos;
    private final List<Coordinate> camps;
    private final Coordinate castle;
    private TablutUtility() {
        gameAshtonTablut = new GameAshtonTablut();
        winningPos = buildWinnningPos();
        camps = buildCamps();
        castle = new Coordinate(4, 4);
    }

    private List<Coordinate> buildWinnningPos(){
        List<Coordinate> winPos = new ArrayList<>();
        winPos.add(new Coordinate(0, 1));
        winPos.add(new Coordinate(0, 2));
        winPos.add(new Coordinate(0, 6));
        winPos.add(new Coordinate(0, 7));
        winPos.add(new Coordinate(8, 1));
        winPos.add(new Coordinate(8, 2));
        winPos.add(new Coordinate(8, 6));
        winPos.add(new Coordinate(8, 7));
        winPos.add(new Coordinate(1, 0));
        winPos.add(new Coordinate(2, 0));
        winPos.add(new Coordinate(6, 0));
        winPos.add(new Coordinate(7, 0));
        winPos.add(new Coordinate(1, 8));
        winPos.add(new Coordinate(2, 8));
        winPos.add(new Coordinate(6, 8));
        winPos.add(new Coordinate(7, 8));
        return winPos;
    }

    private List<Coordinate> buildCamps(){
        List<Coordinate> camps = new ArrayList<>();

        camps.add(new Coordinate(0, 3));
        camps.add(new Coordinate(0, 4));
        camps.add(new Coordinate(1, 4));
        camps.add(new Coordinate(0, 5));

        camps.add(new Coordinate(3, 8));
        camps.add(new Coordinate(4, 8));
        camps.add(new Coordinate(4, 7));
        camps.add(new Coordinate(5, 8));

        camps.add(new Coordinate(8, 3));
        camps.add(new Coordinate(8, 4));
        camps.add(new Coordinate(7, 4));
        camps.add(new Coordinate(8, 5));

        camps.add(new Coordinate(3, 0));
        camps.add(new Coordinate(4, 0));
        camps.add(new Coordinate(4, 1));
        camps.add(new Coordinate(5, 0));
        return camps;
    }
    public static TablutUtility getInstance() {
        if (instance == null) {
            instance = new TablutUtility();
        }
        return instance;
    }

    public IState movePawn(IState state, Action action){
        IState newState = this.applyAction(state, action);
        if (state.getTurn().equalsTurn("W")) {
            newState = gameAshtonTablut.checkCaptureBlack(state, action);
        } else if (state.getTurn().equalsTurn("B")) {
            newState = gameAshtonTablut.checkCaptureWhite(state, action);
        }
        return newState;
    }

    //guarda al TURN in Action e usalo
    private IState applyAction(IState state, Action a) {
        Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
        Pawn[][] board = state.getBoard();
        if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
            board[a.getRowFrom()][a.getColumnFrom()] = Pawn.THRONE;
        } else {
            board[a.getRowFrom()][a.getColumnFrom()] = Pawn.EMPTY;
        }
        board[a.getRowTo()][a.getColumnTo()] = pawn;
        if (state.getTurn() == Turn.WHITE) {
            state.setTurn(Turn.BLACK);
        } else
            state.setTurn(Turn.WHITE);
        return state;
    }

    public <T> void switchOnT(Direction dir, T t, Consumer<T> consumerUp,
                             Consumer<T> consumerDown, Consumer<T> consumerLeft,
                             Consumer<T> consumerRight){
        switch (dir){
            case UP :
                consumerUp.accept(t);
                break;
            case DOWN :
                consumerDown.accept(t);
                break;
            case LEFT :
                consumerLeft.accept(t);
                break;
            case RIGHT :
                consumerRight.accept(t);
                break;
            default: throw new IllegalArgumentException();
        }

    }

    public List<Coordinate> getWinningPos() {
        return winningPos;
    }

    public List<Coordinate> getCamps() {
        return camps;
    }

    public Coordinate getCastle() {
        return castle;
    }
}

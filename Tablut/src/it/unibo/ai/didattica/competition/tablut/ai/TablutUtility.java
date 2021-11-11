package it.unibo.ai.didattica.competition.tablut.ai;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutUtility {

    private static TablutUtility instance = null;
    private GameAshtonTablut gameAshtonTablut;

    private TablutUtility() {
        //aggiunto costruttore di default
        gameAshtonTablut = new GameAshtonTablut();
    }

    public static TablutUtility getInstance() {
        if (instance == null) {
            instance = new TablutUtility();
        }
        return instance;
    }

    public State movePawn(State state, Action action){
        State newState = this.applyAction(state, action);
        if (state.getTurn().equalsTurn("W")) {
            newState = gameAshtonTablut.checkCaptureBlack(state, action);
        } else if (state.getTurn().equalsTurn("B")) {
            newState = gameAshtonTablut.checkCaptureWhite(state, action);
        }
        return newState;
    }


    private State applyAction(State state, Action a) {
        Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
        Pawn[][] board = state.getBoard();
        if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
            board[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
        } else {
            board[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.EMPTY;
        }
        board[a.getRowTo()][a.getColumnTo()] = pawn;
        if (state.getTurn() == Turn.WHITE) {
            state.setTurn(State.Turn.BLACK);
        } else
            state.setTurn(State.Turn.WHITE);
        return state;
    }
}

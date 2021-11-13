package it.unibo.ai.didattica.competition.tablut.ai.utility;

import it.unibo.ai.didattica.competition.tablut.domain.*;

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
}

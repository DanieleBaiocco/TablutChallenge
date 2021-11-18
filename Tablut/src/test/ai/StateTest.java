package test.ai;

import it.unibo.ai.didattica.competition.domain.Pawn;
import it.unibo.ai.didattica.competition.domain.State;
import it.unibo.ai.didattica.competition.domain.StateTablut;
import it.unibo.ai.didattica.competition.domain.Turn;

public class StateTest extends State {
    public StateTest() {
        super();

    }

    public static StateTest generateState1(){
        StateTest state = new StateTest();
        state.board = new Pawn[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                state.board[i][j] = Pawn.EMPTY;
            }
        }

        state.board[4][4] = Pawn.THRONE;

        state.turn = Turn.BLACK;

        state.board[1][7] = Pawn.KING;

        state.board[3][7] = Pawn.WHITE;

        state.board[1][8] = Pawn.BLACK;

        return state;
    }


    public static StateTest generateState2(){
        StateTest state = new StateTest();
        state.board = new Pawn[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                state.board[i][j] = Pawn.EMPTY;
            }
        }

        state.board[4][4] = Pawn.THRONE;

        state.turn = Turn.BLACK;

        state.board[2][7] = Pawn.KING;

        state.board[2][5] = Pawn.WHITE;

        state.board[1][8] = Pawn.BLACK;
        state.board[2][2] = Pawn.BLACK;

        state.board[4][2] = Pawn.WHITE;

        return state;
    }

    public static StateTest generateState3(){
        StateTest state = new StateTest();
        state.board = new Pawn[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                state.board[i][j] = Pawn.EMPTY;
            }
        }

        //minaccia con trono per bianco in 4, 5 con nero attaccato alla posizione da controllare
        state.board[4][4] = Pawn.THRONE;
        state.board[5][4] = Pawn.WHITE;
        state.board[6][5] = Pawn.BLACK;

        state.board[3][3] = Pawn.KING;
        state.board[2][2] = Pawn.WHITE;
        state.board[0][3] = Pawn.BLACK;
        state.board[1][3] = Pawn.WHITE;
        state.turn = Turn.BLACK;

        return state;
    }

        public static StateTest generateStateForMinMax(){
        StateTest state = new StateTest();
        state.board = new Pawn[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                state.board[i][j] = Pawn.EMPTY;
            }
        }

        //minaccia con trono per bianco in 4, 5 con nero attaccato alla posizione da controllare
        state.board[4][4] = Pawn.THRONE;

        state.board[6][5] = Pawn.BLACK;

        state.board[3][3] = Pawn.KING;
        state.board[3][2] = Pawn.BLACK;
        state.board[0][3] = Pawn.BLACK;
        state.board[6][2] = Pawn.BLACK;
        state.turn = Turn.WHITE;

        return state;
    }


    public StateTablut clone() {
        StateTablut result = new StateTablut();

        Pawn oldboard[][] = this.getBoard();
        Pawn newboard[][] = result.getBoard();

        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board[i].length; j++) {
                newboard[i][j] = oldboard[i][j];
            }
        }

        result.setBoard(newboard);
        result.setTurn(this.turn);
        return result;
    }
}

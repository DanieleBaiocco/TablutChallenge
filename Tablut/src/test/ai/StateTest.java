package test.ai;

import java.time.chrono.ThaiBuddhistEra;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.ai.model.Coordinate;
import it.unibo.ai.didattica.competition.ai.utility.Pair;
import it.unibo.ai.didattica.competition.ai.utility.TablutUtility;
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

    public static StateTest generateStartState(){
        StateTest state = new StateTest();
        state.board = new Pawn[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                state.board[i][j] = Pawn.EMPTY;
            }
        }

        state.board[4][4] = Pawn.THRONE;

        state.turn = Turn.WHITE;

        state.board[4][4] = Pawn.KING;

        state.board[4][2] = Pawn.WHITE;
        state.board[4][3] = Pawn.WHITE;
        state.board[4][5] = Pawn.WHITE;
        state.board[4][6] = Pawn.WHITE;
        state.board[2][4] = Pawn.WHITE;
        state.board[3][4] = Pawn.WHITE;
        state.board[5][4] = Pawn.WHITE;
        state.board[6][4] = Pawn.WHITE;

        state.board[0][3] = Pawn.BLACK;
        state.board[0][4] = Pawn.BLACK;
        state.board[0][5] = Pawn.BLACK;
        state.board[1][4] = Pawn.BLACK;
        state.board[3][0] = Pawn.BLACK;
        state.board[4][0] = Pawn.BLACK;
        state.board[5][0] = Pawn.BLACK;
        state.board[4][1] = Pawn.BLACK;
        state.board[8][3] = Pawn.BLACK;
        state.board[8][4] = Pawn.BLACK;
        state.board[8][5] = Pawn.BLACK;
        state.board[7][4] = Pawn.BLACK;
        state.board[3][8] = Pawn.BLACK;
        state.board[4][8] = Pawn.BLACK;
        state.board[5][8] = Pawn.BLACK;
        state.board[4][7] = Pawn.BLACK;

        return state;
    }
    
    public static StateTest generateRandomState(){
        StateTest state = new StateTest();
        List<Coordinate> camps = TablutUtility.getInstance().getCamps();
        state.board = new Pawn[9][9];
        List<Pair<Pawn,Coordinate>> boardStripe = new ArrayList<Pair<Pawn,Coordinate>>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                state.board[i][j] = Pawn.EMPTY;
                boardStripe.add(new Pair<Pawn,Coordinate>(Pawn.EMPTY, new Coordinate(i, j)));
            }
        }
        state.board[4][4] = Pawn.THRONE;
        boardStripe.set(9*4+4, new Pair<>(Pawn.THRONE, new Coordinate(4, 4)));

        //Add white
        Random random = new Random();
        int whiteRemaining = random.nextInt(8);
        for (int i = 1; i<= whiteRemaining; i++){
            List<Pair<Pawn,Coordinate>> possiblePlaces = boardStripe.stream().filter(
                pair -> (pair.getFirst() == Pawn.EMPTY && !camps.contains(pair.getSecond()))
            ).toList();
            int index = random.nextInt(possiblePlaces.size());
            boardStripe.set(9 * possiblePlaces.get(index).getSecond().getRow() + possiblePlaces.get(index).getSecond().getCol(), new Pair<Pawn,Coordinate>(Pawn.WHITE, possiblePlaces.get(index).getSecond()));
        }
        

        //Add black
        int blackRemaining = random.nextInt(16);
        for (int i = 1; i<= blackRemaining; i++){
            List<Pair<Pawn,Coordinate>> possiblePlaces = boardStripe.stream().filter(
                pair -> pair.getFirst() == Pawn.EMPTY
            ).toList();
            int index = random.nextInt(possiblePlaces.size());
            boardStripe.set(9 * possiblePlaces.get(index).getSecond().getRow() + possiblePlaces.get(index).getSecond().getCol(), new Pair<Pawn,Coordinate>(Pawn.BLACK, possiblePlaces.get(index).getSecond()));
        }

        //Add king
        List<Pair<Pawn,Coordinate>> possiblePlaces = boardStripe.stream().filter(
            pair -> (pair.getFirst() == Pawn.EMPTY || pair.getFirst() == Pawn.THRONE) && !camps.contains(pair.getSecond())
        ).toList();
        int index = random.nextInt(possiblePlaces.size());
        boardStripe.set(9 * possiblePlaces.get(index).getSecond().getRow()+possiblePlaces.get(index).getSecond().getCol(), new Pair<Pawn,Coordinate>(Pawn.KING, possiblePlaces.get(index).getSecond()));
        
        boardStripe.stream().forEach(
            pair -> {state.board[pair.getSecond().getRow()][pair.getSecond().getCol()] = pair.getFirst();}
        );

        if (random.nextInt(2) == 0)
            state.turn = Turn.WHITE;
        else 
            state.turn = Turn.BLACK;

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

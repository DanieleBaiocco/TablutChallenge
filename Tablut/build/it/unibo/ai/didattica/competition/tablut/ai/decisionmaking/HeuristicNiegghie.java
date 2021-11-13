package it.unibo.ai.didattica.competition.tablut.ai.decisionmaking;

import it.unibo.ai.didattica.competition.tablut.ai.model.Coordinate;
import it.unibo.ai.didattica.competition.tablut.ai.model.Direction;
import it.unibo.ai.didattica.competition.tablut.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.tablut.ai.utility.Pair;
import it.unibo.ai.didattica.competition.tablut.domain.IState;
import it.unibo.ai.didattica.competition.tablut.domain.Pawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//TODO
public class HeuristicNiegghie {


    static int staticWeights[] = new int[] { 500, // re sulla casella vincente
            -20, // numero di pezzi (minore) fra il re e la fuga in una sola mossa.
            2, // numero di pedine bianche
            -2, // numero di pedine nere
            -5, // numero di pedine nere, castelli o accampamenti direttamente vicino al re
            // (pericolo di cattura)
            +1, // numero di neri che rischiano la cattura
            -1, // numero di mosse fino ad ora
            +1, // numero di bianchi e neri che si fornteggiano(rispetto al re)
            -2, // numero di caselle di fuga con un nero davanti
            +0//???TODO pedine mangiate che liberano una posizione vicino al re
    };

    public HeuristicNiegghie() {
    }

    // non so se effettivamente serve la depth
    public double evaluate(StateDecorator state, int depth) {
        double value = staticWeights[0] * KingEscaped(state)
                + staticWeights[1] * KingToEscape(state)
                + staticWeights[2] * state.getNumberOf(Pawn.WHITE)
                + staticWeights[3] * state.getNumberOf(Pawn.BLACK)
                + staticWeights[4] * KingSurrounded(state)
                + staticWeights[5] * BlackMenaced(state)
                + staticWeights[6] * 0
                + staticWeights[7] * 0
                + staticWeights[8] * EscapesBlocked(state);
        return value;
    }

    /*Ritorna se il re è su una casella di fuga
     */
    //TODO semplifica con State.Turn
    private int KingEscaped(StateDecorator state) {
        List<Coordinate> kingPositions = state.getPieces().get(Pawn.KING);
        if (kingPositions.size() != 1) {
            // Errore
        } else {
            int kingRow = kingPositions.get(0).getRow();
            int kingCol = kingPositions.get(0).getCol();
            int boardDimension = state.getState().getBoard().length;
            if (kingRow == 0 || kingRow == boardDimension - 1 || kingCol == 0 || kingCol == boardDimension - 1) {
                return 1;
            }
        }
        return 0;
    }

    /*Ritorna il valore minore di pezzi che si frappongono fra il re
        e la vittoria in una sola mossa (quindi in linea retta verso la
        casella di fuga). Non conta le mosse verso gli accampamenti
    */
    private int KingToEscape(StateDecorator state) {
        List<Coordinate> kingPositions = state.getPieces().get(Pawn.KING);
        int boardSize = state.getBoard().length;
        if (kingPositions.size() != 1) {
            // !ERRORE. non c'è più il re sulla board oppure ce ne sono troppi
        } else {
            Coordinate kingPosition = kingPositions.get(0);
            int minValue = Integer.MAX_VALUE;
            int pawnCont = 0;
            // guardo su
            if (!state.getCamps().contains(state.getBox(0, kingPosition.getCol()))) {
                pawnCont = 0;
                for (Pair<Coordinate, Pawn> p : state.LookDirection(Direction.UP, kingPosition)) {
                    if (p.getSecond() != Pawn.EMPTY) {
                        pawnCont++;
                    }
                }
                if (minValue > pawnCont)
                    minValue = pawnCont;
            }
            // guardo destra
            if (!state.getCamps().contains(state.getBox(kingPosition.getRow(), boardSize - 1))) {
                pawnCont = 0;
                for (Pair<Coordinate, Pawn>  p : state.LookDirection(Direction.RIGHT, kingPosition)) {
                    if (p.getSecond() != Pawn.EMPTY) {
                        pawnCont++;
                    }
                }
                if (minValue > pawnCont)
                    minValue = pawnCont;
            }
            // guardo giù
            if (!state.getCamps().contains(state.getBox(boardSize - 1, kingPosition.getCol()))) {
                pawnCont = 0;
                for (Pair<Coordinate, Pawn>  p : state.LookDirection(Direction.DOWN, kingPosition)) {
                    if (p.getSecond() != Pawn.EMPTY) {
                        pawnCont++;
                    }
                }
                if (minValue > pawnCont)
                    minValue = pawnCont;
            }
            // guardo sinistra
            if (!state.getCamps().contains(state.getBox(0, kingPosition.getCol()))) {
                pawnCont = 0;
                for (Pair<Coordinate, Pawn>  p : state.LookDirection(Direction.LEFT, kingPosition)) {
                    if (p.getSecond() != Pawn.EMPTY) {
                        pawnCont++;
                    }
                }
                if (minValue > pawnCont)
                    minValue = pawnCont;
            }

            return minValue;
        }
        return 5;
    }

    /*Ritorna il numero di pezzi o strutture che circondano il re e rischiano
        di far avvenire la cattura
    */
    private int KingSurrounded(StateDecorator state){
        int boardSize = state.getBoard().length;
        List<Coordinate> kingPositions = state.getPieces().get(Pawn.KING);
        if (kingPositions.size() == 1){
            Coordinate kingPosition = kingPositions.get(0);
            int cont = 0;
            for (Direction dir : new Direction[]{Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT}){
                Coordinate positionToLook = kingPosition.Look(dir);
                if (positionToLook.getRow() > 0 && positionToLook.getRow() < boardSize
                        && positionToLook.getCol() > 0 && positionToLook.getCol() < boardSize
                        && ((state.getPawn(positionToLook.getRow(), positionToLook.getCol()) != Pawn.EMPTY
                        && state.getPawn(positionToLook.getRow(), positionToLook.getCol()) != Pawn.WHITE)
                        || state.getCamps().contains(positionToLook.getBox()))
                ) cont++;
            }
            return cont;
        }
        return 0;
    }

    /*funzione che guarda ogni nero e controlla se sta per essere mangiato.
    le condizioni sono che deve avere un lato occupato e l'opposto libero
    e deve esserci un pedone bianco che può muoversi e in una sola mossa
    catturare il nero.
    #TODO correggere l'uscita e i break in modo da minimizzare il costo computazionale per non guardare tutte le direzioni
    */
    private int BlackMenaced(StateDecorator state){
        int cont = 0;
        for (Coordinate blackPosition : state.getPieces().get(Pawn.BLACK)){
            //controllo se ho una pedina bianca attaccata
            for (Direction dir : new Direction[]{Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT}){
                Coordinate pawnToLook = blackPosition.Look(dir);
                Pawn p = state.getPawn(pawnToLook.getRow(), pawnToLook.getCol());
                if (p == Pawn.WHITE
                        || p == Pawn.THRONE
                        || p == Pawn.KING
                        || state.getCamps().contains(pawnToLook.getBox()))
                {
                    //siamo nel caso in cui c'è un rischio di cattura. Guardo se esiste un pedone bianco
                    //in grado di mangiarmi

                    int index = dir.getIndex();
                    int oppositeDirectionIndx = (index + 2) % 4;
                    Direction oppositeDirection = Direction.values()[oppositeDirectionIndx];
                    Coordinate capturePosition = blackPosition.Look(Direction.values()[index]);
                    //controllo se l'opposto, cioè la zona che deve venire occupata, non è ancora occupata
                    if (state.getPawn(capturePosition.getRow(), capturePosition.getCol()) == Pawn.EMPTY
                            && !state.getCamps().contains(capturePosition.getBox()))
                    {
                        List<Direction> directions = new ArrayList<Direction>(List.of(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT));
                        directions.remove(dir);
                        //guardo nelle tre direzioni se c'è un pedone bianco che può mangiarmi in una mossa. se c'è conto.
                        //altrimenti se c'è un altro pedone prima, sono salvo da quella direzione.
                        for (Direction secondDir : directions){
                            List<Pair<Coordinate, Pawn>> menacers = state.LookDirection(secondDir, blackPosition.Look(oppositeDirection));
                            for (int distance = 1; distance<menacers.size(); distance++){
                                if (menacers.get(distance-1).getSecond() == Pawn.WHITE || menacers.get(distance-1).getSecond() == Pawn.KING){
                                    cont++;
                                    break;
                                } else if ( menacers.get(distance-1).getSecond() != Pawn.EMPTY || state.getCamps().contains(capturePosition.Look(secondDir, distance).getBox())){
                                    break;
                                }
                            }
                        }
                    } else {
                        //sono salvo perchè ho l'altro lato già occupato
                    }
                }
            }
        }
        return cont;
    }




    /* riporta il numero di caselle di escape che sono occupate o che hanno in line diretta un nero
        come prima pedina visibile
    */
    private int EscapesBlocked(StateDecorator state) {
        Pawn[][] board = state.getBoard();
        int cont = 0;

        //guardo uscite superiori
        for (int j = 1; j < board[0].length - 1; j++ ){
            if (state.getCamps().contains(state.getBox(0, j)))  continue;

            if (state.getPawn(0, j) != Pawn.EMPTY){
                cont++;
            } else {
                List<Pair<Coordinate, Pawn>> pawns = state.LookDirection(Direction.DOWN, new Coordinate(0, j));
                for (Pair p : pawns){
                    if (p.getSecond() == Pawn.BLACK) cont++;
                    if (p.getSecond() == Pawn.EMPTY) continue;
                    else break;
                }
            }
        }

        //guardo uscite laterali destre
        for (int i = 1; i < board.length - 1; i++ ){
            if (state.getCamps().contains(state.getBox(i, board[i].length)))  continue;

            if (state.getPawn(i, board[i].length) != Pawn.EMPTY){
                cont++;
            } else {
                List<Pair<Coordinate, Pawn>> pawns = state.LookDirection(Direction.LEFT, new Coordinate(i, board[i].length));
                for (Pair p : pawns){
                    if (p.getSecond() == Pawn.BLACK) cont++;
                    if (p.getSecond() == Pawn.EMPTY) continue;
                    else break;
                }
            }
        }

        //guardo uscite inferiori
        for (int j = 1; j < board[board.length-1].length - 1; j++ ){
            if (state.getCamps().contains(state.getBox(board.length-1, j)))  continue;

            if (state.getPawn(board.length-1, j) != Pawn.EMPTY){
                cont++;
            } else {
                List<Pair<Coordinate, Pawn>> pawns = state.LookDirection(Direction.UP, new Coordinate(board.length- 1, j));
                for (Pair p : pawns){
                    if (p.getSecond() == Pawn.BLACK) cont++;
                    if (p.getSecond() == Pawn.EMPTY) continue;
                    else break;
                }
            }
        }

        //guardo uscite laterali sinistre
        for (int i = 1; i < board.length - 1; i++ ){
            if (state.getCamps().contains(state.getBox(i, 0)))  continue;

            if (state.getPawn(i, 0) != Pawn.EMPTY){
                cont++;
            } else {
                List<Pair<Coordinate, Pawn>> pawns = state.LookDirection(Direction.RIGHT, new Coordinate(i, 0));
                for (Pair p : pawns){
                    if (p.getSecond() == Pawn.BLACK) cont++;
                    if (p.getSecond() == Pawn.EMPTY) continue;
                    else break;
                }
            }
        }


        return cont;
    }
}

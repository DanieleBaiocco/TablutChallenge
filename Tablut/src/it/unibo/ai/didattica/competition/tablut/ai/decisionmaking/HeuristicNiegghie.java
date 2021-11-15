package it.unibo.ai.didattica.competition.tablut.ai.decisionmaking;

import it.unibo.ai.didattica.competition.tablut.ai.model.Coordinate;
import it.unibo.ai.didattica.competition.tablut.ai.model.Direction;
import it.unibo.ai.didattica.competition.tablut.ai.model.StateDecorator;
import it.unibo.ai.didattica.competition.tablut.ai.utility.Pair;
import it.unibo.ai.didattica.competition.tablut.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.tablut.domain.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.Turn;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HeuristicNiegghie {

    private final List<Coordinate> camps;
    private final List<Coordinate> winningPos;
    private final Coordinate castle;

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

    // TODO metter qui lo stato, metter sopra i pawns black, quelli white e il king :) in modo da chiamare getPieces una sola volta
    public HeuristicNiegghie() {
        this.camps = TablutUtility.getInstance().getCamps();
        this.winningPos = TablutUtility.getInstance().getWinningPos();
        this.castle = TablutUtility.getInstance().getCastle();
    }

    // non so se effettivamente serve la depth
    public double evaluate(StateDecorator state, int depth) {
        double value = staticWeights[0] * winCondition(state)
                + staticWeights[1] * kingToEscape(state)
                + staticWeights[2] * state.getNumberOf(Pawn.WHITE)
                + staticWeights[3] * state.getNumberOf(Pawn.BLACK)
                + staticWeights[4] * kingSurrounded(state)
                + staticWeights[5] * blackMenaced(state)
                + staticWeights[6] * 0
                + staticWeights[7] * 0
                + staticWeights[8] * escapesBlocked(state);
        return value;
    }

    /**
     * checks if either black or white won
     */
    private int winCondition(StateDecorator state) {
        if(state.getTurn() == Turn.WHITEWIN)
            return 1;
        if(state.getTurn() == Turn.BLACKWIN)
            return -1;
        return 0;
    }


    private int kingToEscape(StateDecorator state) {
        List<Coordinate> kingPositions = state.getPieces().get(Pawn.KING);
        Coordinate kingPosition = kingPositions.get(0);
        List<Long> pawnCouts = new ArrayList<>();
        Stream<Direction>  directionStream = buildDirectionBoolStream(state, kingPosition, state.getBoard().length);
        directionStream.forEach(dir -> {
            List<Pair<Coordinate, Pawn>> pawnsInOneDir = state.LookDirection(dir, kingPosition);
            long count = pawnsInOneDir.stream()
                    .filter(pawnPair -> pawnPair.getSecond() != Pawn.EMPTY).count();
            pawnCouts.add(count);
        });
        OptionalInt optionalInt = pawnCouts.stream().mapToInt(Math::toIntExact).min();
        return optionalInt.isPresent() ?  optionalInt.getAsInt() : 6;
    }

    private int winPaths(StateDecorator state){
        final Integer[] winningPaths = {0};
        List<Coordinate> kingPositions = state.getPieces().get(Pawn.KING);
        Coordinate kingPosition = kingPositions.get(0);
        Stream<Direction> directionStream = buildDirectionBoolStream(state, kingPosition, state.getBoard().length);
        directionStream.forEach(dir -> {
             // TODO le posizioni potrebbero essere aggiunte alla lista in modo sbagliato(controllalo)
             List<Pair<Coordinate, Pawn>> pawnsInOneDir = state.LookDirection(dir, kingPosition);
             Optional<Pair<Coordinate, Pawn>> optcordpawn = pawnsInOneDir.stream()
                    .takeWhile(pawnPair -> pawnPair.getSecond() == Pawn.EMPTY)
                     .reduce((x, y) -> y);
             if(optcordpawn.isPresent()){
                 Pair<Coordinate, Pawn> lastcordpawn = optcordpawn.get();
                 if(this.winningPos.contains(lastcordpawn.getFirst()))
                     winningPaths[0]++;
             }
        });
        return winningPaths[0];
    }

    private Stream<Direction> buildDirectionBoolStream(StateDecorator state, Coordinate kingPosition, int boardSize) {
        Map<Direction, Boolean> dirBool = new HashMap<>();
        dirBool.put(Direction.UP, true);
        dirBool.put(Direction.RIGHT, true);
        dirBool.put(Direction.DOWN, true);
        dirBool.put(Direction.LEFT, true);
        updateDirBoolMap(Direction.UP, dirBool,
                camps ->camps.contains(new Coordinate(0, kingPosition.getCol())),
                this.camps );
        updateDirBoolMap(Direction.RIGHT, dirBool,
                camps -> camps.contains(new Coordinate(kingPosition.getRow(), boardSize - 1)),
                this.camps );
        updateDirBoolMap(Direction.DOWN, dirBool,
                camps -> camps.contains(new Coordinate(boardSize - 1, kingPosition.getCol())),
                this.camps );
        updateDirBoolMap(Direction.LEFT, dirBool,
                camps -> camps.contains(new Coordinate(kingPosition.getRow(), 0)),
                this.camps );
        return dirBool.entrySet().stream().filter(Map.Entry::getValue)
                .map(Map.Entry::getKey);
    }

    private void updateDirBoolMap(Direction dir, Map<Direction, Boolean> dirBool,
                                  Predicate<List<Coordinate>> pred, List<Coordinate> camps){
        if(dirBool.get(dir)) {
            if (pred.test(camps)) {
                dirBool.put(dir, false);
                dirBool.put(dir.getOppositeDirection(),false);
            }
        }
    }

    private double kingSurrounded(StateDecorator state){
        Map<Pawn, List<Coordinate>> pieces = state.getPieces();
        Coordinate kingPosition = pieces.get(Pawn.KING).get(0);
        List<Coordinate> blackPieces = pieces.get(Pawn.BLACK);

        Stream<Coordinate> s = Stream.concat(blackPieces.stream(), this.camps.stream());
        double count = s.filter(kingPosition::closeTo).count();
        if(kingPosition.closeTo(this.castle)) count++;
        return count;

    }
    //----

    //piu è alto il contatore piu va a favore del bianco (piu è buona la posizione). Quindi il peso dovrà esser
    //positivo. Nel caso simmetrico il peso sarà negativo perchè premia il nero.

    /*funzione che guarda ogni nero e controlla se sta per essere mangiato.
    le condizioni sono che deve avere un lato occupato da un bianco/una base/un trono (?) e l'opposto libero
    e deve esserci un pedone bianco che può muoversi e in una sola mossa
    catturare il nero.
    #TODO correggere l'uscita e i break in modo da minimizzare il costo computazionale per non guardare tutte le direzioni
    */
    private int blackMenaced(StateDecorator state) {
       return menaced(Pawn.BLACK, pawn -> pawn == Pawn.WHITE || pawn == Pawn.KING, state);
    }

    private int whiteMenaced(StateDecorator state){
        return menaced(Pawn.WHITE, pawn -> pawn == Pawn.BLACK, state);
    }

    private int menaced(Pawn pawnToCheck, Predicate<Pawn> pred, StateDecorator state){
        List<Coordinate> blackPieces = state.getPieces().get(pawnToCheck);
        int countToReturn = 0;
        for (Coordinate blackCoord : blackPieces) {
            for (Direction dir : Direction.values()) {
                Pair<Coordinate, Pawn> pairToLook = blackCoord.look(dir, state);
                if (pred.test(pairToLook.getSecond()) || pairToLook.getSecond() == Pawn.THRONE
                        || this.camps.contains(pairToLook.getFirst())) {
                    Direction oppositeDirection = dir.getOppositeDirection();
                    Pair<Coordinate, Pawn> pairCapturePosition = blackCoord.look(oppositeDirection, state);
                    if (pairCapturePosition.getSecond() == Pawn.EMPTY
                            && !this.camps.contains(pairCapturePosition.getFirst())) {
                        List<Direction> captureDirs = Arrays.stream(Direction.values()).collect(Collectors.toList());
                        captureDirs.remove(dir);
                        for (Direction captureDir : captureDirs) {
                            List<Pair<Coordinate, Pawn>> menacers = state.LookDirection(captureDir, pairCapturePosition.getFirst());
                            //TODO gestisci caso in cui c'è un bianco ma alla prima posizione ( in quel caso non c'è rischio cattura ).
                            Optional<Pair<Coordinate, Pawn>> opt = menacers.stream().takeWhile(pair -> pair.getSecond() == Pawn.EMPTY).reduce((x, y) -> y);
                            Pair<Coordinate, Pawn> pointOfStop = opt.get();
                            //TODO controlla che non sto sul bordo
                            Pair<Coordinate, Pawn> pairNearToPointOfStop = pointOfStop.getFirst().look(captureDir, state);
                            if (pred.test(pairNearToPointOfStop.getSecond())) {
                                countToReturn++;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return countToReturn;
    }


    /* riporta il numero di caselle di escape che sono occupate o che hanno in line diretta un nero
        come prima pedina visibile
    */
    private int escapesBlocked(StateDecorator state) {
        Pawn[][] board = state.getBoard();
        int cont = 0;

        //guardo uscite superiori
        for (int j = 1; j < board[0].length - 1; j++ ){
            if (camps.contains(new Coordinate(0, j)))  continue;

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
            if (camps.contains(new Coordinate(i, board[i].length)))  continue;

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
            if (camps.contains(new Coordinate(board.length-1, j)))  continue;

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
            if (camps.contains(new Coordinate(i, 0)))  continue;

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

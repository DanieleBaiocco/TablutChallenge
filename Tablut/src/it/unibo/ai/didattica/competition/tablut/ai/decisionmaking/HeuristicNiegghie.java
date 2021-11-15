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
    private final StateDecorator state;
    private final List<Coordinate> whitePawns;
    private final List<Coordinate> blackPawns;
    private final Coordinate king;

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

    public HeuristicNiegghie(StateDecorator state) {
        this.camps = TablutUtility.getInstance().getCamps();
        this.winningPos = TablutUtility.getInstance().getWinningPos();
        this.castle = TablutUtility.getInstance().getCastle();
        this.state = state;
        Map<Pawn, List<Coordinate>> listMap = state.getPieces();
        this.whitePawns = listMap.get(Pawn.WHITE);
        this.blackPawns = listMap.get(Pawn.BLACK);
        this.king = listMap.get(Pawn.KING).get(0);

    }

    //TODO CAPIRE SE SERVE DEPTH
    public double evaluate() {
        double value = staticWeights[0] * winCondition()
                + staticWeights[1] * kingToEscape()
                + staticWeights[2] * this.whitePawns.size()
                + staticWeights[3] * this.blackPawns.size()
                + staticWeights[4] * kingSurrounded()
                + staticWeights[5] * blackMenaced()
                + staticWeights[6] * 0
                + staticWeights[7] * 0
                + staticWeights[8] * escapesBlocked();
        return value;
    }

    /**
     * checks if either black or white won
     */
    private int winCondition() {
        if(this.state.getTurn() == Turn.WHITEWIN)
            return 1;
        if(this.state.getTurn() == Turn.BLACKWIN)
            return -1;
        return 0;
    }


    private int kingToEscape() {
        List<Long> pawnCouts = new ArrayList<>();
        Stream<Direction>  directionStream = buildDirectionBoolStream(this.king, this.state.getBoard().length);
        directionStream.forEach(dir -> {
            List<Pair<Coordinate, Pawn>> pawnsInOneDir = this.state.LookDirection(dir, this.king);
            long count = pawnsInOneDir.stream()
                    .filter(pawnPair -> pawnPair.getSecond() != Pawn.EMPTY || this.camps.contains(pawnPair.getFirst())).count();
            pawnCouts.add(count);
        });
        OptionalInt optionalInt = pawnCouts.stream().mapToInt(Math::toIntExact).min();
        return optionalInt.isPresent() ?  optionalInt.getAsInt() : 6;
    }

    private int winPaths(){
        final Integer[] winningPaths = {0};
        Stream<Direction> directionStream = buildDirectionBoolStream( this.king, this.state.getBoard().length);
        directionStream.forEach(dir -> {
             // TODO le posizioni potrebbero essere aggiunte alla lista in modo sbagliato(controllalo)
             List<Pair<Coordinate, Pawn>> pawnsInOneDir = this.state.LookDirection(dir, this.king);
             Optional<Pair<Coordinate, Pawn>> optCordPawn = pawnsInOneDir.stream()
                    .takeWhile(pawnPair -> pawnPair.getSecond() == Pawn.EMPTY && !this.camps.contains(pawnPair.getFirst()))
                     .reduce((x, y) -> y);
             if(optCordPawn.isPresent()){
                 Pair<Coordinate, Pawn> lastcordpawn = optCordPawn.get();
                 if(this.winningPos.contains(lastcordpawn.getFirst()))
                     winningPaths[0]++;
             }
        });
        return winningPaths[0];
    }


    private Stream<Direction> buildDirectionBoolStream(Coordinate kingPosition, int boardSize) {
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

    private double kingSurrounded(){
        Stream<Coordinate> s = Stream.concat(this.blackPawns.stream(), this.camps.stream());
        double count = s.filter(this.king::closeTo).count();
        if(this.king.closeTo(this.castle)) count++;
        return count;
    }
    //----

    //piu è alto il contatore piu va a favore del bianco (piu è buona la posizione). Quindi il peso dovrà esser
    //positivo. Nel caso simmetrico il peso sarà negativo perchè premia il nero.

    /*funzione che guarda ogni nero e controlla se sta per essere mangiato.
    le condizioni sono che deve avere un lato occupato da un bianco/una base/un trono (?) e l'opposto libero
    e deve esserci un pedone bianco che può muoversi e in una sola mossa
    catturare il nero.
    */
    private int blackMenaced() {
       return menaced(this.blackPawns, pawn -> pawn == Pawn.WHITE || pawn == Pawn.KING);
    }

    private int whiteMenaced(){
        return menaced(this.whitePawns, pawn -> pawn == Pawn.BLACK);
    }

    private int menaced(List<Coordinate> coloredPieces, Predicate<Pawn> pred){
        int countToReturn = 0;
        for (Coordinate coord : coloredPieces) {
            for (Direction dir : Direction.values()) {
                Pair<Coordinate, Pawn> pairToLook = coord.look(dir, this.state);
                if (pred.test(pairToLook.getSecond()) || pairToLook.getSecond() == Pawn.THRONE
                        || this.camps.contains(pairToLook.getFirst())) {
                    Direction oppositeDirection = dir.getOppositeDirection();
                    Pair<Coordinate, Pawn> pairCapturePosition = coord.look(oppositeDirection, this.state);
                    if (pairCapturePosition.getSecond() == Pawn.EMPTY
                            && !this.camps.contains(pairCapturePosition.getFirst())) {
                        List<Direction> captureDirs = Arrays.stream(Direction.values()).collect(Collectors.toList());
                        captureDirs.remove(dir);
                        for (Direction captureDir : captureDirs) {
                            List<Pair<Coordinate, Pawn>> menacers = this.state.LookDirection(captureDir, pairCapturePosition.getFirst());
                            //TODO gestisci caso in cui c'è un bianco ma alla prima posizione ( in quel caso non c'è rischio cattura ).
                            Optional<Pair<Coordinate, Pawn>> opt = menacers.stream().takeWhile(pair -> pair.getSecond() == Pawn.EMPTY).reduce((x, y) -> y);
                            Pair<Coordinate, Pawn> pointOfStop = opt.get();
                            //TODO controlla che non sto sul bordo
                            Pair<Coordinate, Pawn> pairNearToPointOfStop = pointOfStop.getFirst().look(captureDir, this.state);
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

    /*
    riporta il numero di caselle di escape che sono occupate o che hanno in line diretta un nero
        come prima pedina visibile
    */
    private int escapesBlocked() {
        int count = 0;
        int length = this.state.getBoard().length;
        List<Coordinate> winPosUp = this.winningPos.stream().filter(c -> c.getRow() == 0).collect(Collectors.toList());
        List<Coordinate> winPosDx = this.winningPos.stream().filter(c -> c.getCol() == length-1).collect(Collectors.toList());
        List<Coordinate> winPosDown = this.winningPos.stream().filter(c -> c.getRow() == length -1).collect(Collectors.toList());
        List<Coordinate> winPosSx = this.winningPos.stream().filter(c -> c.getCol() == 0).collect(Collectors.toList());
        count += escapesBlockedCalculus(winPosUp, Direction.DOWN);
        count += escapesBlockedCalculus(winPosDx, Direction.LEFT);
        count += escapesBlockedCalculus(winPosDown, Direction.UP);
        count += escapesBlockedCalculus(winPosSx, Direction.RIGHT);
        return count;
    }

    private int escapesBlockedCalculus(List<Coordinate> winPositions, Direction dir) {
        int countToReturn = 0;
        for(Coordinate winPos : winPositions){
            if(this.state.getPawn(winPos.getRow(), winPos.getCol()) != Pawn.EMPTY)
                countToReturn++;
            else{
                List<Pair<Coordinate, Pawn>> pawns = this.state.LookDirection(dir, winPos);
                for (Pair<Coordinate, Pawn> p : pawns){
                    if(p.getSecond() == Pawn.BLACK){
                        countToReturn++;
                        break;
                    }
                }
            }
        }
        return countToReturn;
    }
}

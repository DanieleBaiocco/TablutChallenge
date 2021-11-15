package it.unibo.ai.didattica.competition.tablut.ai.model;

import it.unibo.ai.didattica.competition.tablut.ai.utility.Pair;
import it.unibo.ai.didattica.competition.tablut.ai.utility.TablutUtility;
import it.unibo.ai.didattica.competition.tablut.domain.IState;
import it.unibo.ai.didattica.competition.tablut.domain.Pawn;

import java.util.Objects;

public class Coordinate {
    private int row;
    private int col;

    public Coordinate(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getBox(){
        String ret;
        char col = (char) (this.col + 97);
        ret = col + "" + (this.row + 1);
        return ret;
    }

    public Pair<Coordinate, Pawn> look(Direction dir, IState state){
        return look(dir, 1, state);
    }
    public Pair<Coordinate, Pawn> look (Direction dir, int distance, IState state){
        Coordinate c = new Coordinate(this.row, this.col);
        Pair<Coordinate, Pawn> pair = new Pair<>(c, Pawn.EMPTY);
        TablutUtility.getInstance().switchOnT(dir, pair,
                pa -> {
                    pa.getFirst().setRow(this.row - distance);
                    updatePairWithPawn(pa, state);
                    },
                pa -> {
                    pa.getFirst().setRow(this.row + distance);
                    updatePairWithPawn(pa, state);
                            },
                pa -> {
                    pa.getFirst().setCol(this.col - distance);
                    updatePairWithPawn(pa, state);
                },
                pa -> {
                    pa.getFirst().setCol(this.col + distance);
                    updatePairWithPawn(pa, state);
                });
        return pair;
    }

    private void updatePairWithPawn(Pair<Coordinate, Pawn> pa, IState state) {
        Pawn pawnToInsert = state.getPawn(pa.getFirst().row, pa.getFirst().col);
        pa.setSecond(pawnToInsert);
    }


    public boolean closeTo(Coordinate other){
        if(other.getCol() < 0 || other.getRow() < 0)
            return false;

        int res = this.getCol() - other.getCol() + this.getRow() - other.getRow();
        return (res == 1 || res == -1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate coord = (Coordinate) o;
        return getRow() == coord.getRow() &&
                getCol() == coord.getCol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol());
    }
}

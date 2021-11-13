package it.unibo.ai.didattica.competition.tablut.ai.model;

import it.unibo.ai.didattica.competition.tablut.ai.utility.TablutUtility;

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



    //capisci se è corretto
     public Coordinate Look(Direction dir, int distance){
        Coordinate c = new Coordinate(this.row, this.col);
         TablutUtility.getInstance().switchOnT(dir, c,
                 cord -> cord.setRow(this.row - distance),
                 cord -> cord.setRow(this.row + distance),
                 cord -> cord.setCol(this.col - distance),
                 cord -> cord.setCol(this.col + distance));
        return c;
    }


    public Coordinate Look(Direction dir){
        return Look(dir, 1);
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

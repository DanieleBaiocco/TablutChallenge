package it.unibo.ai.didattica.competition.tablut.ai.model;

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



     public Coordinate Look(Direction dir, int distance){
        Coordinate c = new Coordinate(this.row, this.col);
        if (dir == Direction.UP){
            c.setRow(this.row - distance);
        } else if (dir == Direction.RIGHT){
            c.setCol(this.col + distance);
        } else if (dir == Direction.DOWN){
            c.setRow(this.row + distance);
        } else if (dir == Direction.LEFT){
            c.setCol(this.col - distance);
        }
        return c;
    }


    public Coordinate Look(Direction dir){
        return Look(dir, 1);
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

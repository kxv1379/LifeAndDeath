package com.project.LifeAndDeth;

public class VO{//한수 쉼은 x,y 값 = -1;
    private int x;
    private int y;
    private int color;
    private int moveCount;//PRIMARY_KEY
    
    public VO(int x,int y,int color,int moveCount){
        this.x = x;
        this.y = y;
        this.color = color;
        this.moveCount = moveCount;
    }
    public VO(int x,int y,int color){
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }
}

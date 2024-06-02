package com.kristalsoft.kastenblock;


public class Food {
    private int x, y;

    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void relocate(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
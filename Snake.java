package com.kristalsoft.kastenblock;

import java.util.LinkedList;

public class Snake {
    private LinkedList<int[]> body;
    private int dx, dy;

    public Snake(int startX, int startY) {
        body = new LinkedList<>();
        body.add(new int[]{startX, startY});
        dx = 1; // Initial direction to the right
        dy = 0;
    }

    public void move() {
        int[] head = body.getFirst();
        int newX = head[0] + dx;
        int newY = head[1] + dy;
        body.addFirst(new int[]{newX, newY});
        body.removeLast();
    }

    public void grow() {
        int[] tail = body.getLast();
        body.addLast(new int[]{tail[0] - dx, tail[1] - dy});
    }

    public void setDirection(int dx, int dy) {
        // Prevent the snake from reversing
        if (this.dx != -dx || this.dy != -dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public LinkedList<int[]> getBody() {
        return body;
    }

    public boolean checkCollision(int maxWidth, int maxHeight) {
        int[] head = body.getFirst();
        // Check boundary collision
        if (head[0] < 0 || head[0] >= maxWidth || head[1] < 0 || head[1] >= maxHeight) {
            return true;
        }
        // Check self-collision
        for (int i = 1; i < body.size(); i++) {
            if (head[0] == body.get(i)[0] && head[1] == body.get(i)[1]) {
                return true;
            }
        }
        return false;
    }
}

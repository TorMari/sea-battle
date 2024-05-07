package com.example.newProject.models;

import org.springframework.stereotype.Component;

@Component
public class PlayerBoard extends BattleShipBoard {

    protected void placeShip(int x, int y, int length, boolean horizontal) {
        Cell[][] cells = getCells();
        for (int i = 0; i < length; i++) {
            if (horizontal) {
                cells[x + i][y].setState(Cell.State.SHIP);
                cells[x + i][y].setShipSize(length);
                cells[x + i][y].updateStyle();
            } else {
                cells[x][y + i].setState(Cell.State.SHIP);
                cells[x][y + i].setShipSize(length);
                cells[x][y + i].updateStyle();
            }
        }
    }
}


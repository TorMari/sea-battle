package com.example.newProject.models;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public abstract class BattleShipBoard extends VerticalLayout {
    private static final int BOARD_SIZE = 10;
    private static final int[] SHIP_LENGTHS = {1, 1, 1, 1, 2, 2, 2, 3, 3, 4};
    private static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final String[] NUMBERS = {"1 ", "2 ", "3 ", "4 ", "5 ", "6 ", "7 ", "8 ", "9 ", "10"};
    private final Cell[][] cells = new Cell[BOARD_SIZE][BOARD_SIZE];

    public Cell[][] getCells() {
        return cells;
    }

    public static int getBoardSize() {
        return BOARD_SIZE;
    }

    public BattleShipBoard() {
        createBoard();
    }

    private void createBoard() {
        setSpacing(false);
        setPadding(false);
        //addClassName("grid");


        Div lettersRow = new Div();
        lettersRow.addClassName("row");
        Div firstCell = new Div();
        firstCell.addClassName("cell-label");
        lettersRow.add(firstCell);
        for (String letter : LETTERS) {
            Div letterCell = new Div();
            letterCell.addClassName("cell-label");
            letterCell.setText(letter);
            lettersRow.add(letterCell);
        }
        add(lettersRow);

        for (int i = 0; i < BOARD_SIZE; i++) {
            Div row = new Div();
            row.addClassName("row");


            Div numberCell = new Div();
            numberCell.addClassName("cell-label");
            numberCell.setText(NUMBERS[i]);
            row.add(numberCell);

            add(row);
            for (int j = 0; j < BOARD_SIZE; j++) {
                Cell cell = new Cell();
                this.cells[i][j] = cell;
                row.add(cell);
            }
        }

        placeShipsRandomly();
    }

    public void placeShipsRandomly() {
        cleanField();
        Random random = new Random();
        for (int length : SHIP_LENGTHS) {
            boolean shipPlaced = false;
            while (!shipPlaced) {
                int x = random.nextInt(BOARD_SIZE);
                int y = random.nextInt(BOARD_SIZE);
                boolean horizontal = random.nextBoolean();
                if (canPlaceShip(x, y, length, horizontal)) {
                    placeShip(x, y, length, horizontal);
                    shipPlaced = true;
                }
            }
        }
    }

    private boolean canPlaceShip(int x, int y, int length, boolean horizontal) {
        int xEnd = horizontal ? x + length - 1 : x;
        int yEnd = horizontal ? y : y + length - 1;

        if (xEnd >= BOARD_SIZE || yEnd >= BOARD_SIZE) {
            return false;
        }

        for (int i = Math.max(0, x - 1); i <= Math.min(BOARD_SIZE - 1, xEnd + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(BOARD_SIZE - 1, yEnd + 1); j++) {
                if (this.cells[i][j].getState() != Cell.State.EMPTY) {
                    return false;
                }
            }
        }

        return true;
    }

    protected abstract void placeShip(int x, int y, int lenght, boolean horizontal);

    private void cleanField() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.cells[i][j].setState(Cell.State.EMPTY);
            }
        }
    }

    public void updateStyles() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.cells[i][j].updateStyle();
            }
        }
    }
    public void hideShips() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                this.cells[i][j].updateStyle("rgb(245, 245, 245, 0.2)");
            }
        }
    }
}



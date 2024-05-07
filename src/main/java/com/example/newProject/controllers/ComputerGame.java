package com.example.newProject.controllers;
import java.util.List;
import java.util.ArrayList;


import com.example.newProject.models.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

import com.vaadin.flow.router.Router;

@Component
public class ComputerGame extends HorizontalLayout {

    private static final int BOARD_SIZE = 10;
    private BattleShipBoard playerBoard;
    private BattleShipBoard computerBoard;
    private PlayersInfo players;
    private boolean playerTurn;
    private boolean gameOver;
    private int[] scores;
    private H3 player;
    private H3 computer;
    public ComputerGame(PlayersInfo players) {
        initializeGame(players);
        startGame();
    }


    private void initializeGame(PlayersInfo players) {
        this.playerBoard = new PlayerBoard();
        this.computerBoard = new ComputerBoard();
        this.players = players;
        this.scores = new int[]{0,0};
        setSpacing(false);
        setPadding(false);

        this.playerTurn = true;
        this.gameOver = false;

        VerticalLayout playerLayout = new VerticalLayout();
        player = new H3(String.format("%s: %d", players.getPlayer1(), players.getGameScore()[0]));
        player.getStyle().setColor("#FFFFFF").setFontSize("36px");
        playerLayout.add(player, playerBoard);

        VerticalLayout computerLayout = new VerticalLayout();
        computer = new H3(String.format("%s: %d", players.getPlayer2(), players.getGameScore()[1]));
        computer.getStyle().setColor("#FFFFFF").setFontSize("36px");
        computerLayout.add(computer, computerBoard);
        computerLayout.getStyle().set("visibility", "hidden");

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.getStyle().setMarginTop("200px");

        Button randomButton = new Button("Replace ships");
        randomButton.addClassName("action-button");
        randomButton.addClickListener(event -> {
            if(playerTurn) {
                //System.out.println("place");
                playerBoard.placeShipsRandomly();
                playerBoard.updateStyles();
            }
        });

        Button randomMoveButton = new Button("Random shot");
        randomMoveButton.addClassName("action-button");
        randomMoveButton.addClickListener(event -> {
            if(playerTurn && !gameOver) {
                System.out.println("place");
                makeRandomTurn(computerBoard);
                //playerTurn = false;
            }
        });

        Button nextButton = new Button("Start");
        nextButton.addClassName("action-button");
        nextButton.addClickListener(event -> {
            //System.out.println(playerTurn);
            //playerBoard.setVisible(true);
            computerLayout.getStyle().set("visibility", "visible");
            randomButton.setVisible(false);
            nextButton.setVisible(false);
            buttonLayout.add(randomMoveButton);
        });

        Button backButton = new Button("Go to menu");
        backButton.addClassName("action-button");
        backButton.addClickListener(event -> {
            goBackAlert();
        });

        buttonLayout.add(randomButton, nextButton, backButton);
        add(playerLayout, buttonLayout, computerLayout);
    }

    private void startGame() {
        Cell[][] cells = computerBoard.getCells();
        //int[] k = new int[]{0, 0};
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Cell cell = cells[i][j];
                // cell.canClick();
                int x = i;
                int y = j;
                cell.addClickListener(e -> {
                    if (!gameOver && playerTurn){
                        if (cell.getState() == Cell.State.SHIP) {
                            cell.setState(Cell.State.HIT);
                            cell.updateStyle();
                            isShipSunk(cells, x, y);
                            this.scores[0]++;
                            player.setText(String.format("%s: %d", players.getPlayer1(), players.getGameScore()[0]));
                            if (this.scores[0] == 20) {
                                players.setGameScore(++players.getGameScore()[0], players.getGameScore()[1]);
                                showWinnerNotification();
                            }
                        } else if (cell.getState() == Cell.State.EMPTY) {
                            cell.setState(Cell.State.MISS);
                            cell.updateStyle();
                            computerTurn();//computerBoard.makeTurn(playerBoard, k[1]);
                        }
                    }
                });
            }
        }
    }

    private void makeRandomTurn(BattleShipBoard playerBoard){
        Random random = new Random();
        boolean turnMade = false;
        while (!turnMade) {
            int x = random.nextInt(BOARD_SIZE);
            int y = random.nextInt(BOARD_SIZE);
            Cell[][] cells = playerBoard.getCells();
            if (cells[x][y].getState() == Cell.State.SHIP) {
                cells[x][y].setState(Cell.State.HIT);
                isShipSunk(cells, x, y);
                //int k = playerTurn ? 1 : 0;
                this.scores[0]++;
                player.setText(String.format("%s: %d", players.getPlayer1(), players.getGameScore()[0]));
                if(this.scores[0] == 20) {
                    players.setGameScore(++players.getGameScore()[0], players.getGameScore()[1]);
                    showWinnerNotification();
                }
                cells[x][y].updateStyle();
                turnMade = true;
                //computerTurn();
            } else if (cells[x][y].getState() == Cell.State.EMPTY) {
                cells[x][y].setState(Cell.State.MISS);
                cells[x][y].updateStyle();
                turnMade = true;
                computerTurn();
                //playerTurn = !playerTurn;
            }
        }
    }


    public void computerTurn() {
        Random random = new Random();
        boolean turnMade = false;
        while (!turnMade) {
            int x = random.nextInt(BOARD_SIZE);
            int y = random.nextInt(BOARD_SIZE);
            Cell[][] cells = playerBoard.getCells();
            if (cells[x][y].getState() == Cell.State.SHIP) {
                cells[x][y].setState(Cell.State.HIT);
                isShipSunk(cells, x, y);
                this.scores[1]++;
                computer.setText(String.format("%s: %d", players.getPlayer2(), players.getGameScore()[1]));
                if(this.scores[1] == 20) {
                    players.setGameScore(players.getGameScore()[0], ++players.getGameScore()[1]);
                    showWinnerNotification();
                }
                cells[x][y].updateStyle();
                turnMade = true;
                //computerTurn();
                smartComputerTurn(cells, x, y);
            } else if (cells[x][y].getState() == Cell.State.EMPTY) {
                cells[x][y].setState(Cell.State.MISS);
                cells[x][y].updateStyle();
                turnMade = true;
            }
        }
    }

    private void smartComputerTurn(Cell[][] cells, int x, int y) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int xCoordinate = x + dx;
                int yCoordinate = y + dy;
                if (xCoordinate >= 0 && xCoordinate < BOARD_SIZE &&
                        yCoordinate >= 0 && yCoordinate < BOARD_SIZE &&
                        cells[xCoordinate][yCoordinate].getState() == Cell.State.SHIP) {
                    cells[xCoordinate][yCoordinate].setState(Cell.State.HIT);
                    cells[xCoordinate][yCoordinate].updateStyle();
                    isShipSunk(cells, x, y);
                    this.scores[1]++;
                    computer.setText(String.format("%s: %d", players.getPlayer2(), players.getGameScore()[1]));
                    if(this.scores[1] == 20) {
                        players.setGameScore(players.getGameScore()[0], ++players.getGameScore()[1]);
                        showWinnerNotification();
                    }
                    //break;
                    smartComputerTurn(cells, xCoordinate, yCoordinate);
                }
            }
        }
    }

    private void showWinnerNotification() {
        gameOver = true;

        Dialog winnerDialog = new Dialog();

        winnerDialog.setHeaderTitle("Game over!");
        winnerDialog.setWidth("470px");

        H3 playersNameText = new H3(String.format("%s - %s", players.getPlayer1(), players.getPlayer2()));
        H3 playersScoreText = new H3(String.format("%d - %d", players.getGameScore()[0], players.getGameScore()[1]));

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button backButton = new Button("Go to menu", event -> {
            goBackAlert();
        });
        backButton.addClassName("action-button");

        Button playButton = new Button("Play on", event -> {
            VaadinSession.getCurrent().setAttribute("players", players);
            removeAll();
            initializeGame(players);
            startGame();
        });
        playButton.addClassName("action-button");
        buttonLayout.getStyle().setMarginTop("12px");

        buttonLayout.add(backButton, playButton);
        winnerDialog.add(playersNameText, playersScoreText, buttonLayout);
        winnerDialog.setCloseOnOutsideClick(false);
        winnerDialog.open();
        add(winnerDialog);
    }



    private void isShipSunk(Cell[][] cells, int x, int y) {

        List<int[]> shipCoordinates = new ArrayList<>();
        shipCoordinates.add(new int[]{x, y});
        System.out.println(cells[x][y].getShipSize());

        int shipLength = 1;
        int left = x - 1;
        while (left >= 0 && cells[left][y].getState() == Cell.State.HIT) {
            shipCoordinates.add(new int[]{left, y});
            shipLength++;
            left--;
        }
        int right = x + 1;
        while (right < BOARD_SIZE && cells[right][y].getState() == Cell.State.HIT) {
            shipCoordinates.add(new int[]{right, y});
            shipLength++;
            right++;
        }

        int up = y - 1;
        while (up >= 0 && cells[x][up].getState() == Cell.State.HIT) {
            shipCoordinates.add(new int[]{x, up});
            shipLength++;
            up--;
        }
        int down = y + 1;
        while (down < BOARD_SIZE && cells[x][down].getState() == Cell.State.HIT) {
            shipCoordinates.add(new int[]{x, down});
            shipLength++;
            down++;
        }
        if(shipLength == cells[x][y].getShipSize()) {
            drawGreyZone(cells, shipCoordinates);
        }
    }

    private void drawGreyZone(Cell[][] cells, List<int[]> shipCoordinates) {
        for (int[] c : shipCoordinates) {
            //System.out.println(c[0] + " " + c[1]);
            cells[c[0]][c[1]].setState(Cell.State.KILLED);
            cells[c[0]][c[1]].updateStyle();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int xCoordinate = c[0] + dx;
                    int yCoordinate = c[1] + dy;
                    if (xCoordinate >= 0 && xCoordinate < BOARD_SIZE &&
                            yCoordinate >= 0 && yCoordinate < BOARD_SIZE &&
                            cells[xCoordinate][yCoordinate].getState() == Cell.State.EMPTY) {
                        cells[xCoordinate][yCoordinate].setState(Cell.State.MISS);
                        cells[xCoordinate][yCoordinate].updateStyle();
                    }
                }
            }
        }
    }

    private void goBackAlert() {
        Dialog alertDialog = new Dialog();

        alertDialog.setHeaderTitle("Attention!");

        H5 alertText = new H5("Are you sure you want to surrender to the computer?");

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button cancelButton = new Button("Cancel", event -> {
            alertDialog.close();
        });
        cancelButton.addClassName("action-button");

        Button backButton = new Button("Go to menu", event -> {
            VaadinSession.getCurrent().setAttribute("players", null);
            getUI().ifPresent(ui -> ui.navigate(""));
        });
        backButton.addClassName("action-button");
        buttonLayout.getStyle().setMarginTop("12px");

        buttonLayout.add(cancelButton, backButton);
        alertDialog.add(alertText, buttonLayout);
        alertDialog.setCloseOnOutsideClick(false);
        alertDialog.open();
        add(alertDialog);
    }
}



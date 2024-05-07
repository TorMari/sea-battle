package com.example.newProject.controllers;

import com.example.newProject.models.BattleShipBoard;
import com.example.newProject.models.Cell;
import com.example.newProject.models.PlayerBoard;
import com.example.newProject.models.PlayersInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class PlayerGame extends HorizontalLayout {
    private static final int BOARD_SIZE = 10;
    private BattleShipBoard player1Board;
    private BattleShipBoard player2Board;
    private PlayersInfo players;
    private boolean playerTurn;
    private boolean gameOver;
    private int[] scores;
    private H3 player1;
    private H3 player2;
    public PlayerGame(PlayersInfo players) {
        initializeGame(players);
        startGame();
    }

    private void initializeGame(PlayersInfo players) {
        this.player1Board = new PlayerBoard();
        this.player2Board = new PlayerBoard();
        this.players = players;
        this.scores = new int[]{0, 0};
        setSpacing(false);
        setPadding(false);

        this.playerTurn = true;
        this.gameOver = false;

        VerticalLayout player1Layout = new VerticalLayout();
        player1 = new H3(String.format("%s: %d", players.getPlayer1(), players.getGameScore()[0]));
        player1.getStyle().setColor("#FFFFFF").setFontSize("36px");
        player1Layout.add(player1, player1Board);

        VerticalLayout player2Layout = new VerticalLayout();
        player2 = new H3(String.format("%s: %d", players.getPlayer2(), players.getGameScore()[1]));
        player2.getStyle().setColor("#FFFFFF").setFontSize("36px");
        player2Layout.add(player2, player2Board);
        player2Layout.getStyle().set("visibility", "hidden");

        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.getStyle().setMarginTop("200px");

        Button randomButton = new Button("Replace ships");
        randomButton.addClassName("action-button");
        randomButton.addClickListener(event -> {
            if(playerTurn) {
                player1Board.placeShipsRandomly();
                player1Board.updateStyles();
            } else {
                player2Board.placeShipsRandomly();
                player2Board.updateStyles();
            }
        });

        Button randomMoveButton = new Button("Random shot");
        randomMoveButton.addClassName("action-button");
        randomMoveButton.addClickListener(event -> {
            if(playerTurn && !gameOver) {
                makeRandomTurn(player2Board);
                //playerTurn = false;
            } else if (!playerTurn && !gameOver) {
                //System.out.println("place2");
                makeRandomTurn(player1Board);
                //playerTurn = true;
            }
        });

        Button nextButton = new Button("Next");
        nextButton.addClassName("action-button");
        nextButton.addClickListener(event -> {
            //System.out.println(playerTurn);
            if(playerTurn) {
                //player2Layout.setVisible(true);
                //player1Layout.setVisible(false);
                player2Layout.getStyle().set("visibility", "visible");
                player1Layout.getStyle().set("visibility", "hidden");
                playerTurn = false;
                nextButton.setText("Start");
            } else {
                //player2Layout.setVisible(true);
                //player1Layout.setVisible(true);
                player2Layout.getStyle().set("visibility", "visible");
                player1Layout.getStyle().set("visibility", "visible");
                player1Board.hideShips();
                player2Board.hideShips();
                randomButton.setVisible(false);
                nextButton.setVisible(false);
                playerTurn = true;
                buttonLayout.add(randomMoveButton);
            }
        });

        Button backButton = new Button("Go to menu");
        backButton.addClassName("action-button");
        backButton.addClickListener(event -> {
            goBackAlert();
        });

        buttonLayout.add(randomButton, nextButton, backButton);
        add(player1Layout, buttonLayout, player2Layout);
    }

    private void startGame(){
        Cell[][] player1 = player1Board.getCells();
        Cell[][] player2 = player2Board.getCells();
        //int[] k = new int[]{0, 0};
        /*for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(playerTurn) {
                    cell = player1[i][j];
                } else {
                    cell = player2[i][j];
                }
                // cell.canClick(); // Делаем ячейку кликабельной
                int x = i;
                int y = j;
                cell.addClickListener(e -> {
                    if (!gameOver){
                        if (cell.getState() == Cell.State.SHIP) {
                            cell.setState(Cell.State.HIT);
                            cell.updateStyle();
                            System.out.println("win");
                            if(isShipSunk(cells, x, y)){
                                System.out.println("killed");
                            }
                            else {
                                System.out.println("kill it");
                            }
                            isShipSunk(cells, x, y);
                            this.scores[0]++;
                            if (this.scores[0] == 20) {
                                showWinnerNotification("You are winner!");
                            }
                            //k[1] = computerTurn(k[1]);//computerBoard.makeTurn(playerBoard, k[1]);
                            //startGame();
                        } else if (cell.getState() == Cell.State.EMPTY) {
                            cell.setState(Cell.State.MISS);
                            cell.updateStyle();
                            computerTurn();//computerBoard.makeTurn(playerBoard, k[1]);
                        }
                    }
                });
            }
        }*/

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Cell player1Cell = player1[i][j];
                Cell player2Cell = player2[i][j];
                int x = i;
                int y = j;
                player2Cell.addClickListener(e -> {
                    if (!gameOver && playerTurn) {
                        handlePlayerTurn(player2Cell, player2, x, y);
                    }
                });

                player1Cell.addClickListener(e -> {
                    if (!gameOver && !playerTurn) {
                        handlePlayerTurn(player1Cell, player1, x, y);
                    }
                });
            }
        }
    }

    private void handlePlayerTurn(Cell cell, Cell[][] board, int x, int y) {
        if (cell.getState() == Cell.State.SHIP) {
            cell.setState(Cell.State.HIT);
            cell.updateStyle();
            isShipSunk(board, x, y);
            //int k = playerTurn ? 1 : 0;
            if(playerTurn) {
                this.scores[0]++;
                this.player1.setText(String.format("%s: %d", players.getPlayer1(), players.getGameScore()[0]));
                if (this.scores[0] == 20) {
                    players.setGameScore(++players.getGameScore()[0], players.getGameScore()[1]);
                    showWinnerNotification();
                }
            } else {
                this.scores[1]++;
                this.player2.setText(String.format("%s: %d", players.getPlayer2(), players.getGameScore()[1]));
                if (this.scores[1] == 20) {
                    players.setGameScore(players.getGameScore()[0], ++players.getGameScore()[1]);
                    showWinnerNotification();
                }
            }


            //k[1] = computerTurn(k[1]);//computerBoard.makeTurn(playerBoard, k[1]);
            //startGame();
        } else if (cell.getState() == Cell.State.EMPTY) {
            cell.setState(Cell.State.MISS);
            cell.updateStyle();
            playerTurn = !playerTurn;
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
                //this.scores[k]++;
                //if(this.scores[k] == 20) {
                  //  showWinnerNotification();
                //}
                if(playerTurn) {
                    this.scores[0]++;
                    this.player1.setText(String.format("%s: %d", players.getPlayer1(), players.getGameScore()[0]));
                    if (this.scores[0] == 20) {
                        players.setGameScore(++players.getGameScore()[0], players.getGameScore()[1]);
                        showWinnerNotification();
                    }
                } else {
                    this.scores[1]++;
                    this.player2.setText(String.format("%s: %d", players.getPlayer2(), players.getGameScore()[1]));
                    if (this.scores[1] == 20) {
                        players.setGameScore(players.getGameScore()[0], ++players.getGameScore()[1]);
                        showWinnerNotification();
                    }
                }
                cells[x][y].updateStyle();
                turnMade = true;
                //computerTurn();
            } else if (cells[x][y].getState() == Cell.State.EMPTY) {
                cells[x][y].setState(Cell.State.MISS);
                cells[x][y].updateStyle();
                turnMade = true;
                playerTurn = !playerTurn;
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
            //String parametr = UUID.randomUUID().toString();
            removeAll();
            //getUI().ifPresent(ui -> ui.navigate("computer-game"));
            //String uniqueParam = UUID.randomUUID().toString();
            //getUI().ifPresent(ui -> ui.navigate("computer-game/"));
            initializeGame(players);
            startGame();
            //winnerDialog.close();
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
            //System.out.println(coordinates[0] + " " + coordinates[1]);
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

        H5 alertText = new H5("Are you sure you want to surrender to your opponent?");

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



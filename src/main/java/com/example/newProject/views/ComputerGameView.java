package com.example.newProject.views;

import com.example.newProject.controllers.ComputerGame;
import com.example.newProject.models.PlayersInfo;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;


@Route("computer-game")
@CssImport("/css/styles.css")
public class ComputerGameView extends HorizontalLayout {

    @Autowired
    public ComputerGameView() {
        PlayersInfo players = (PlayersInfo) VaadinSession.getCurrent().getAttribute("players");
        if(players != null) {
            ComputerGame computerGame = new ComputerGame(players);
            add(computerGame);
        } else {
            Dialog registrationDialog = new Dialog();
            registrationDialog.setHeaderTitle("Game with computer");
            //registrationDialog.getStyle().setColor("#CD853F");
            registrationDialog.setWidth("470px");

            FormLayout formLayout = new FormLayout();
            TextField nicknameField = new TextField("Your nickname");
            formLayout.add(nicknameField);

            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button backButton = new Button("Go to menu");
            backButton.addClassName("action-button");
            backButton.addClickListener(e -> {
                VaadinSession.getCurrent().setAttribute("players", null);
                getUI().ifPresent(ui -> ui.navigate(""));
            });

            Button compButton = new Button("Play with computer", e -> {
                String playerName = nicknameField.getValue();
                if (!playerName.isEmpty()) {
                    PlayersInfo newPlayers = new PlayersInfo();
                    newPlayers.setPlayer1(playerName);
                    newPlayers.setPlayer2("Computer");
                    ComputerGame computerGame = new ComputerGame(newPlayers);
                    add(computerGame);
                    registrationDialog.close();
                } else {
                    Notification.show("Please enter your username", 3000, Notification.Position.TOP_CENTER);
                }
            });
            compButton.addClassName("action-button");
            buttonLayout.getStyle().setMarginTop("12px");

            buttonLayout.add(backButton, compButton);
            registrationDialog.add(formLayout, buttonLayout);
            registrationDialog.setCloseOnOutsideClick(false);
            registrationDialog.open();
            add(registrationDialog);
        }
    }
}



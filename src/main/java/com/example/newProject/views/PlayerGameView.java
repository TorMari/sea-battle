package com.example.newProject.views;

import com.example.newProject.controllers.PlayerGame;
import com.example.newProject.models.PlayersInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("players-game")
@CssImport("/css/styles.css")
//@CssImport("css/player-styles.css")
public class PlayerGameView extends HorizontalLayout {
    @Autowired
    public PlayerGameView() {
        PlayersInfo players = (PlayersInfo) VaadinSession.getCurrent().getAttribute("players");
        if(players != null) {
            PlayerGame playerGame = new PlayerGame(players);
            add(playerGame);
        } else {
            Dialog registrationDialog = new Dialog();
            registrationDialog.setHeaderTitle("Game with friend");
            registrationDialog.setWidth("470px");

            FormLayout formLayout1 = new FormLayout();
            TextField nicknameField1 = new TextField("Your nickname");
            formLayout1.add(nicknameField1);

            FormLayout formLayout2 = new FormLayout();
            TextField nicknameField2 = new TextField("Your friend`s nickname");
            formLayout2.add(nicknameField2);

            HorizontalLayout buttonLayout = new HorizontalLayout();

            Button backButton = new Button("Go to menu");
            backButton.addClassName("action-button");
            backButton.addClickListener(e -> {
                VaadinSession.getCurrent().setAttribute("players", null);
                getUI().ifPresent(ui -> ui.navigate(""));
            });

            Button playButton = new Button("Play with friend", e -> {
                String playerName1 = nicknameField1.getValue();
                String playerName2 = nicknameField2.getValue();
                if (!playerName1.isEmpty() && !playerName2.isEmpty()) {
                    PlayersInfo newPlayers = new PlayersInfo();
                    newPlayers.setPlayer1(playerName1);
                    newPlayers.setPlayer2(playerName2);
                    PlayerGame playerGame = new PlayerGame(newPlayers);
                    add(playerGame);
                    registrationDialog.close();
                } else {
                    Notification.show("Please enter usernames", 3000, Notification.Position.TOP_CENTER);
                }
            });
            playButton.addClassName("action-button");
            buttonLayout.getStyle().setMarginTop("12px");

            buttonLayout.add(backButton, playButton);
            registrationDialog.add(formLayout1, formLayout2, buttonLayout);
            registrationDialog.setCloseOnOutsideClick(false);
            registrationDialog.open();
            add(registrationDialog);
        }
    }
}
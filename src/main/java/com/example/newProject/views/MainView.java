package com.example.newProject.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.button.Button;


@Route("")
@CssImport("/css/styles.css")
public class MainView extends VerticalLayout {
    @Autowired
    public MainView() {
        VaadinSession.getCurrent().setAttribute("players", null);
        setAlignItems(Alignment.CENTER);

        H1 name = new H1("Welcome to Sea Battle!");
        name.getStyle().setFontSize("48px");
        name.getStyle().setColor("#FFFFFF");
        H3 descr = new H3("Choose your opponent");
        //name.getStyle().setFontSize("48px");
        descr.getStyle().setColor("#FFFFFF");
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.getStyle().setMarginTop("100px");
        //buttons.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Button compButton = new Button("Play with computer", event -> {
            getUI().ifPresent(ui -> ui.navigate("computer-game"));
        });
        compButton.addClassName("choice-button");

        Button playButton = new Button("Play with friend", event -> {
            getUI().ifPresent(ui -> ui.navigate("players-game"));
        });
        playButton.addClassName("choice-button");
        buttons.add(compButton, playButton);
        add(name, descr, buttons);
    }

}



package com.example.newProject.models;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

public class Cell extends Div {
    private State state;
    private int shipSize;

    public enum State {
        EMPTY,
        SHIP,
        MISS,
        HIT,
        KILLED
    }

    public Cell() {
        this.state = State.EMPTY;
        addClassName("cell");
    }

    public int getShipSize() {
        return shipSize;
    }

    public void setShipSize(int shipSize) {
        this.shipSize = shipSize;
    }


    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void updateStyle() {
        getStyle().set("background-color", getColorForState(state));
    }

    public void updateStyle(String color) {
        getStyle().set("background-color", color);
    }

    private String getColorForState(State state) {
        switch (state) {
            case EMPTY:
                return "rgb(245, 245, 245, 0.2)"; //"#FAF0E6";
            case SHIP:
                return "#191970";
            case HIT:
                return "#FF3131";
            case KILLED:
                return "#880808";
            case MISS:
                return "#778899";
            default:
                return "rgb(245, 245, 245, 0.2)";//"#FAF0E6";
        }
    }
}

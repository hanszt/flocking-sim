package hzt.model.custom_controls;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class MultipleEventsHandler implements EventHandler<ActionEvent> {

    private final List<EventHandler<ActionEvent>> actionEventHandlers;

    MultipleEventsHandler() {
        actionEventHandlers = new ArrayList<>();
    }

    public void addEventHandler(EventHandler<ActionEvent> handler) {
        this.actionEventHandlers.add(handler);
    }

    public void addEventHandler(int index, EventHandler<ActionEvent> handler) {
        this.actionEventHandlers.add(index, handler);
    }

    public void removeEventHandler(EventHandler<ActionEvent> handler) {
        this.actionEventHandlers.remove(handler);
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        for (EventHandler<ActionEvent> handler : actionEventHandlers) {
            handler.handle(actionEvent);
        }
    }
}

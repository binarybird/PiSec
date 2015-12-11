package pisec.gui.event.ui;

import pisec.gui.control.UIController;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class CloseEvent<T extends UIController>  extends Event {
    public static final EventType<CloseEvent> CLOSE_EVENT = new EventType<>("CLOSE_EVENT");

    private T controller = null;
    private String message = null;

    public CloseEvent(T controller, String message) {
        super(CLOSE_EVENT);
        this.controller = controller;
        this.message = message;
    }

    public CloseEvent(T controller, String message, @NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, CLOSE_EVENT);
        this.controller = controller;
    }

    public T getEventSourceController(){return controller;}
    public String getMessage(){return message;}
}

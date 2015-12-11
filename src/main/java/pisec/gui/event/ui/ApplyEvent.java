package pisec.gui.event.ui;

import pisec.gui.control.UIController;
import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class ApplyEvent<T extends UIController> extends Event {
    public static final EventType<ApplyEvent> APPLY_EVENT = new EventType<>("APPLY_EVENT");

    private T controller = null;
    private String message = null;

    public ApplyEvent(T controller,String message) {
        super(APPLY_EVENT);
        this.controller = controller;
        this.message = message;
    }

    public ApplyEvent(T controller,String message, @NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, APPLY_EVENT);
        this.controller = controller;
    }

    public T getEventSourceController(){return controller;}
    public String getMessage(){return message;}
}

package pisec.gui.event.alarm;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class DisarmEvent extends Event {
    public static final EventType<DisarmEvent> DISARM_EVENT = new EventType<>("DISARM_EVENT");

    private String message = null;

    public DisarmEvent(String message) {
        super(DISARM_EVENT);
        this.message = message;
    }

    public DisarmEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, DISARM_EVENT);
    }

    public String getMessage(){return message;}

}

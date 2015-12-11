package pisec.gui.event.alarm;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class DisarmingEvent extends Event {
    public static final EventType<DisarmingEvent> DISARMING_EVENT = new EventType<>("DISARMING_EVENT");

    private String message = null;


    public DisarmingEvent(String message) {
        super(DISARMING_EVENT);
        this.message = message;
    }

    public DisarmingEvent(String message, @NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, DISARMING_EVENT);
        this.message = message;
    }

    public void setMessage(String message){this.message = message;}
    public String getMessage(){return message;}


}

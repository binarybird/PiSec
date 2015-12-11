package pisec.gui.event.alarm;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class ArmingEvent extends Event {
    public static final EventType<ArmingEvent> ARMING_EVENT = new EventType<>("ARMING_EVENT");

    private String message = null;


    public ArmingEvent(String message) {
        super(ARMING_EVENT);
        this.message = message;
    }

    public ArmingEvent(String message, @NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, ARMING_EVENT);
        this.message = message;
    }

    public void setMessage(String message){this.message = message;}
    public String getMessage(){return message;}


}

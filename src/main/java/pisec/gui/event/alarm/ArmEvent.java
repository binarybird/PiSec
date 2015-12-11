package pisec.gui.event.alarm;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class ArmEvent extends Event  {
    public static final EventType<ArmEvent> ARM_EVENT = new EventType<>("ARM_EVENT");

    private String message = null;

    public ArmEvent(String message) {
        super(ARM_EVENT);
        this.message = message;
    }

    public ArmEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, ARM_EVENT);
    }

    public String getMessage(){return message;}

}

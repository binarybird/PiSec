package pisec.gui.event.alarm;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import pisec.security.AlarmManager;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class AlarmEvent extends Event  {
    public static final EventType<AlarmEvent> ALARM_EVENT = new EventType<>("ALARM_EVENT");

    private String message = null;
    private AlarmManager.Alarm alarm = null;

    public AlarmEvent(String message, AlarmManager.Alarm alarm) {
        super(ALARM_EVENT);
        this.message = message;
        this.alarm = alarm;
    }

    public AlarmEvent(@NamedArg("source") Object source, @NamedArg("target") EventTarget target) {
        super(source, target, ALARM_EVENT);
    }

    public String getMessage(){return message;}
    public AlarmManager.Alarm getAlarm(){return alarm;}

}

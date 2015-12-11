package pisec.gui.control;

import pisec.gui.event.alarm.*;
import pisec.gui.event.ui.ApplyEvent;
import pisec.gui.event.ui.CancelEvent;
import pisec.gui.event.ui.CloseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public abstract class UIController extends AnchorPane {

    public UIController(){
        this.addEventHandler(CloseEvent.CLOSE_EVENT,this::handleCloseEvent);
        this.addEventHandler(ApplyEvent.APPLY_EVENT,this::handleApplyEvent);
        this.addEventHandler(CancelEvent.CANCEL_EVENT,this::handleCancelEvent);

        this.addEventHandler(ArmEvent.ARM_EVENT,this::handleArmEvent);
        this.addEventHandler(DisarmEvent.DISARM_EVENT,this::handleDisarmEvent);
        this.addEventHandler(ArmingEvent.ARMING_EVENT,this::handleArmingEvent);
        this.addEventHandler(DisarmingEvent.DISARMING_EVENT,this::handleDisarmingEvent);
        this.addEventHandler(AlarmEvent.ALARM_EVENT,this::handleAlarmEvent);
    }

    public abstract void handleCloseEvent(CloseEvent<?> event);
    public abstract void handleApplyEvent(ApplyEvent<?> event);
    public abstract void handleCancelEvent(CancelEvent<?> event);

    public abstract void handleArmEvent(ArmEvent event);
    public abstract void handleArmingEvent(ArmingEvent event);
    public abstract void handleDisarmEvent(DisarmEvent event);
    public abstract void handleDisarmingEvent(DisarmingEvent event);
    public abstract void handleAlarmEvent(AlarmEvent event);

}

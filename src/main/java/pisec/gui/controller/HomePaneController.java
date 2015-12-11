package pisec.gui.controller;



import pisec.gui.concurrency.TimerManager;
import pisec.gui.control.UIController;
import pisec.gui.event.alarm.*;
import pisec.gui.event.ui.ApplyEvent;
import pisec.gui.event.ui.CancelEvent;
import pisec.gui.event.ui.CloseEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import pisec.security.AlarmManager;
import pisec.util.Logger;
import pisec.util.enums.AlarmState;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class HomePaneController extends UIController{

    @FXML
    Label timeLabel;

    @FXML
    Label alarmStateLabel;

    public HomePaneController(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomePane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException(exception);
        }

    }

    public void setAlarmStateLabel(AlarmState state){
        Platform.runLater(()->{switch(state){
            case ALARMED:alarmStateLabel.setText("Alarm Triggered!");break;
            case ARMED:alarmStateLabel.setText("Armed");break;
            case ARMING:alarmStateLabel.setText("Arming...");break;
            case DISARMED:alarmStateLabel.setText("Disarmed");break;
            case DISARMING:alarmStateLabel.setText("Disarming...");break;
            case COOLDOWN:alarmStateLabel.setText("Cooldown...");break;
            default:break;
        }});

    }

    @FXML
    private void initialize(){
        TimerManager.GetSharedTimerManager().createSchedualedTask(1000,task-> Platform.runLater(()-> timeLabel.setText(new SimpleDateFormat("hh:mm:ss").format(new Date()))));
    }

    @Override
    public void handleCloseEvent(CloseEvent<?> event) {

    }

    @Override
    public void handleApplyEvent(ApplyEvent<?> event) {

    }

    @Override
    public void handleCancelEvent(CancelEvent<?> event) {

    }

    @Override
    public void handleArmEvent(ArmEvent event){
        this.setAlarmStateLabel(AlarmManager.GetSharedAlarmManager().getAlarmState());
    }

    @Override
    public void handleArmingEvent(ArmingEvent event) {
        this.setAlarmStateLabel(AlarmManager.GetSharedAlarmManager().getAlarmState());
    }

    @Override
    public void handleDisarmEvent(DisarmEvent event) {
        this.setAlarmStateLabel(AlarmManager.GetSharedAlarmManager().getAlarmState());
    }

    @Override
    public void handleDisarmingEvent(DisarmingEvent event) {
        this.setAlarmStateLabel(AlarmManager.GetSharedAlarmManager().getAlarmState());
    }

    @Override
    public void handleAlarmEvent(AlarmEvent event) {
        this.setAlarmStateLabel(AlarmManager.GetSharedAlarmManager().getAlarmState());
    }

    public String toString(String message){
        return HomePaneController.class.getName()+"[\" EventMessage:\""+message+"\"]";
    }

    @Override
    public String toString(){
        return this.toString("");
    }

}

package pisec.security;

import pisec.device.Camera;
import pisec.gui.concurrency.TimerManager;
import pisec.gui.control.UIMessage;
import pisec.gui.controller.RootPaneController;
import pisec.gui.event.alarm.*;
import pisec.security.auth.AuthRequest;
import pisec.security.auth.AuthValidator;
import pisec.security.auth.Token;
import pisec.util.Flags;
import pisec.util.Logger;
import pisec.util.SendEmail;
import pisec.util.Settings;
import pisec.util.enums.AlarmMode;
import pisec.util.enums.AlarmState;
import pisec.util.enums.Setting;
import pisec.util.enums.Zone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class AlarmManager {

    private AlarmState currentState = AlarmState.DISARMED;
    private AlarmState previousState = null;
    private AlarmMode currentMode = AlarmMode.NOTSET;
    private AlarmMode previousMode = AlarmMode.NOTSET;
    private static AlarmManager sharedAlarmManager = null;
    private final static Stack<Alarm> alarms = new Stack<>();
    private static final Object requestLock = new Object();
    private static final Object stateLock = new Object();

    private AlarmManager(){}

    public static AlarmManager GetSharedAlarmManager(){
        synchronized (requestLock) {
            if (sharedAlarmManager == null) {
                sharedAlarmManager = new AlarmManager();
                sharedAlarmManager.stateControl(AlarmState.DISARMED, AlarmMode.NOTSET);
            }
        }
        return sharedAlarmManager;
    }

    public AlarmState getAlarmState(){synchronized (requestLock) {return currentState;}}

    public AlarmMode getAlarmMode(){synchronized (requestLock) {return currentMode;}}

    public boolean requestArm_Stay(final Token token){return requestArm(AlarmMode.STAY,token);}

    public boolean requestArm_Instant(final Token token){return requestArm(AlarmMode.INSTANT,token);}

    public Stack<Alarm> getPreviousAlarms(){
        final Stack<Alarm> ret = new Stack<>();
        alarms.forEach(e->ret.add(e.clone()));

        return ret;
    }

    public boolean requestDisarm(final Token token){
        synchronized (requestLock) {
            boolean ret = false;
            final AuthRequest authRequest = new AuthRequest(token, action -> {
                RootPaneController.GetSharedRootPaneController().fireEvent(new DisarmEvent("Disarmed"));
                stateControl(AlarmState.DISARMED, AlarmMode.NOTSET);
            });
            ret = AuthValidator.performRequest(authRequest);
            return ret;
        }
    }

    public boolean requestAlarm(final String message, final Date date, final Zone zone){
        synchronized (requestLock) {
            boolean ret = false;

            if (getAlarmState() == AlarmState.DISARMING || getAlarmState() == AlarmState.DISARMED)
                return false;

            if(getAlarmState() == AlarmState.ALARMED){
                createAlarm(message, date, zone);
                return true;
            }

            if (currentMode == AlarmMode.INSTANT) {
                stateControl(AlarmState.ALARMED, currentMode);
                final Alarm alarm = createAlarm(message, date, zone);
                RootPaneController.GetSharedRootPaneController().fireEvent(new AlarmEvent("Alarm - Instant", alarm));
                ret = true;
            } else {

                stateControl(AlarmState.DISARMING, currentMode);
                RootPaneController.GetSharedRootPaneController().fireEvent(new DisarmingEvent("Disarming"));

                final int armTime = Integer.valueOf(Settings.GetSettingForKey(Setting.ALARM_DISARM_TIME));
                ret = true;
                TimerManager.GetSharedTimerManager().createCountDown(armTime, timeIn -> {
                    RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Disarm " + timeIn), true);
                    if (timeIn == 0 && AlarmManager.GetSharedAlarmManager().getAlarmState() != AlarmState.DISARMED) {
                        stateControl(AlarmState.ALARMED, currentMode);
                        final Alarm alarm = createAlarm(message, date, zone);
                        RootPaneController.GetSharedRootPaneController().fireEvent(new AlarmEvent("Alarm - Stay", alarm));
                    }
                });
            }
            return ret;
        }
    }

    public boolean purgeAlarms(final Token token){
        final boolean[] ret = {false};
        AuthRequest request = new AuthRequest(token,action->{
            alarms.clear();
            ret[0] = true;
        });
        AuthValidator.performRequest(request);

        return ret[0];
    }

    private Alarm createAlarm(final String message,final Date date,final Zone zone){
        Alarm alarm = new Alarm(message,date,zone,Camera.TakePicture());
        if(!Flags.IsSet(Flags.TESTMODE))
            SendEmail.send(alarm);
        alarms.add(alarm);
        return alarm;
    }

    private boolean requestArm(final AlarmMode mode,final Token token){
        synchronized (requestLock) {
            boolean ret = false;
            final AuthRequest authRequest = new AuthRequest(token, action -> {
                RootPaneController.GetSharedRootPaneController().fireEvent(new ArmingEvent("Arming - " + mode));
                if (stateControl(AlarmState.ARMING, mode)) {
                    createArmTimer(mode, "Armed - " + mode);
                }
            });
            ret = AuthValidator.performRequest(authRequest);
            return ret;
        }
    }

    private void createArmTimer(final AlarmMode mode,final String eventMessage) {
        final int armTime = Integer.valueOf(Settings.GetSettingForKey(Setting.ALARM_ARM_TIME));
        TimerManager.GetSharedTimerManager().createCountDown(armTime, intIn -> {
            RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Leave " + intIn), true);
            if (intIn == 0 && getAlarmState() == AlarmState.ARMING) {
                RootPaneController.GetSharedRootPaneController().fireEvent(new ArmEvent(eventMessage));
                stateControl(AlarmState.ARMED, mode);
            } else if (intIn == 0 && getAlarmState() == AlarmState.DISARMED) {
                RootPaneController.GetSharedRootPaneController().fireEvent(new DisarmEvent("Disarmed"));
            }
        });
    }

    private boolean stateControl(final AlarmState newState,final AlarmMode newMode){
        synchronized (stateLock) {
            boolean ret = false;
            if (AuthValidator.getCooldown())
                return ret;

            switch (currentState) {
                case DISARMED:
                    switch (newState) {
                        case DISARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ALARMED:
                            break;
                        case ARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case COOLDOWN:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ARMING:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case DISARMING:
                            break;
                        default:
                            break;
                    }
                    break;
                case DISARMING:
                    switch (newState) {
                        case DISARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ALARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ARMED:
                        case COOLDOWN:
                        case ARMING:
                        case DISARMING:
                        default:
                            break;
                    }
                    break;
                case ALARMED:
                    switch (newState) {
                        case DISARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ALARMED:
                        case ARMED:
                        case COOLDOWN:
                        case ARMING:
                        case DISARMING:
                        default:
                            break;
                    }
                    break;
                case ARMED:
                    switch (newState) {
                        case DISARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ALARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ARMED:
                        case COOLDOWN:
                        case ARMING:
                        case DISARMING:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                        default:
                            break;
                    }
                    break;
                case ARMING:
                    switch (newState) {
                        case DISARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ALARMED:
                        case COOLDOWN:
                        case ARMING:
                        case DISARMING:
                        default:
                            break;
                    }
                    break;
                case COOLDOWN:
                    switch (newState) {
                        case DISARMED:
                            previousMode = currentMode;
                            currentMode = newMode;
                            previousState = currentState;
                            currentState = newState;
                            ret = true;
                            break;
                        case ALARMED:
                        case ARMED:
                        case COOLDOWN:
                        case ARMING:
                        case DISARMING:
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }


            if (ret) {
                Logger.Log(this.toString());
            } else {
                Logger.Log(AlarmManager.class.getName() + "[Error:\"IllegalStateChange\" CurrentState:\"" + currentState + "\" NewState:\"" + newState + "\"]");
            }
            RootPaneController.GetSharedRootPaneController().getHomePaneController().setAlarmStateLabel(currentState);

            return ret;
        }
    }

    public class Alarm {
        private Date alarmDate = null;
        private String message = null;
        private Zone zone = Zone.NONE;
        private String pictureLocation = "";

        private Alarm(final String message,final Date date,final Zone zone,final String imageSaveLocation){
            this.alarmDate = date;
            this.message = message;
            this.zone = zone;
            this.pictureLocation = imageSaveLocation;
        }
        public Date getDate(){
            return alarmDate;
        }
        public String getMessage(){
            return message;
        }
        public Zone getZone(){
            return zone;
        }
        public String getPictureLocation(){return pictureLocation;}

        @Override
        public Alarm clone(){
            return new Alarm(this.message,(Date)this.alarmDate.clone(),this.zone,this.pictureLocation);
        }

        @Override
        public String toString(){
            return Alarm.class.getName()+"[Date:\""+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(alarmDate)+"\", Message:\""+message+"\", Zone:\""+zone.toString()+"\", Images:\""+pictureLocation+"\"]";
        }
    }

    @Override
    public String toString(){
        return AlarmManager.class.getName()+"[State:\""+currentState+"\" Mode:\""+currentMode+"\" AlarmCount:\""+alarms.size()+"\"]";
    }
}

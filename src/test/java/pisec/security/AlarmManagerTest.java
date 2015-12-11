package pisec.security;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pisec.security.auth.AuthValidator;
import pisec.util.Flags;
import pisec.util.Settings;
import pisec.util.enums.AlarmMode;
import pisec.util.enums.AlarmState;
import pisec.util.enums.Setting;
import pisec.util.enums.Zone;

import java.util.Date;

/**
 * Created by jamesrichardson on 10/30/15.
 */
public class AlarmManagerTest {
    private static final int secondsToSleep_Arm = (Integer.valueOf(Settings.GetSettingForKey(Setting.ALARM_ARM_TIME)) + 1) * 1000;
    private static final int secondsToSleep_Disarm = (Integer.valueOf(Settings.GetSettingForKey(Setting.ALARM_DISARM_TIME)) + 1) * 1000;

    private static AlarmManager manager = null;

    public static class AsNonApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    @BeforeClass
    public static void initJFX() {
        Flags.SetFlag(Flags.TESTMODE);
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(AsNonApp.class, new String[0]);

            }
        };
        t.setDaemon(true);
        t.start();
        manager=AlarmManager.GetSharedAlarmManager();//must come AFTER javafx is started
    }

    @Test
    public void testRequestStay(){
        reset();

        //arm - stay
        boolean ret2 = manager.requestArm_Stay(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.STAY, manager.getAlarmMode());
    }

    @Test
    public void testRequestStayDisarm(){
        reset();

        //arm - stay
        boolean ret2 = manager.requestArm_Stay(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.STAY, manager.getAlarmMode());

        //set off an alarm - timer for "stay"
        manager.requestAlarm("",new Date(),Zone.TEST);
        Assert.assertEquals(AlarmState.DISARMING,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.STAY, manager.getAlarmMode());

        //disarm the alarm
        boolean ret3 = manager.requestDisarm(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret3);
        Assert.assertEquals(AlarmState.DISARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.NOTSET, manager.getAlarmMode());
    }

    @Test
    public void testRequestStayAlarm(){
        reset();

        //arm - stay
        boolean ret2 = manager.requestArm_Stay(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.STAY, manager.getAlarmMode());

        //set off an alarm - timer for "stay"
        manager.requestAlarm("",new Date(),Zone.TEST);
        Assert.assertEquals(AlarmState.DISARMING,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.STAY, manager.getAlarmMode());

        //dont disarm the alarm
        try {Thread.sleep(secondsToSleep_Disarm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ALARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.STAY, manager.getAlarmMode());
    }

    @Test
    public void testRequestInstant(){
        reset();

        //arm - instant
        boolean ret2 = manager.requestArm_Instant(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.INSTANT, manager.getAlarmMode());
    }

    @Test
    public void testRequestInstantDisarm(){
        reset();

        //arm - instant
        boolean ret2 = manager.requestArm_Instant(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.INSTANT, manager.getAlarmMode());

        //disarm - instant
        boolean ret3 = manager.requestDisarm(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret3);
        Assert.assertEquals(AlarmState.DISARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.NOTSET, manager.getAlarmMode());
    }

    @Test
    public void testRequestInstantAlarm(){
        reset();

        //arm - instant
        boolean ret2 = manager.requestArm_Instant(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.INSTANT, manager.getAlarmMode());

        //set off alarm - "instant"
        manager.requestAlarm("Test Triggered!", new Date(), Zone.TEST);
        Assert.assertEquals(AlarmState.ALARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.INSTANT, manager.getAlarmMode());

        //disarm the alarm
        boolean ret3 = manager.requestDisarm(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret3);
        Assert.assertEquals(AlarmState.DISARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.NOTSET, manager.getAlarmMode());
    }

    @Test
    public void testRequestDisarm(){
        //test disarm
        boolean ret = manager.requestDisarm(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret);
        Assert.assertEquals(AlarmState.DISARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.NOTSET, manager.getAlarmMode());
    }

    @Test
    public void testRequestRaiseAlarm(){
        reset();

        //set the alarm
        boolean ret2 = manager.requestArm_Instant(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.INSTANT, manager.getAlarmMode());

        //trigger alarm
        manager.requestAlarm("Test Triggered!", new Date(), Zone.TEST);
        Assert.assertNotEquals(0,manager.getPreviousAlarms().size());
        manager.purgeAlarms(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
    }

    @Test
    public void testPurgeAlarms(){
        reset();

        //set alarm
        boolean ret2 = manager.requestArm_Instant(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret2);
        Assert.assertEquals(AlarmState.ARMING,manager.getAlarmState());
        try {Thread.sleep(secondsToSleep_Arm);}catch(Exception e){}
        Assert.assertEquals(AlarmState.ARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.INSTANT, manager.getAlarmMode());

        //trigger alarm
        manager.requestAlarm("Test Triggered!", new Date(), Zone.TEST);
        Assert.assertNotEquals(0,manager.getPreviousAlarms().size());

        //purge alarm
        manager.purgeAlarms(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(0,manager.getPreviousAlarms().size());
    }

    private void reset(){
        boolean ret = manager.requestDisarm(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(true,ret);
        Assert.assertEquals(AlarmState.DISARMED,manager.getAlarmState());
        Assert.assertEquals(AlarmMode.NOTSET, manager.getAlarmMode());

        manager.purgeAlarms(AuthValidator.generateAuthToken(Settings.getDebugPassword()));
        Assert.assertEquals(0,manager.getPreviousAlarms().size());
    }

}

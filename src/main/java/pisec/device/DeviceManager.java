package pisec.device;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import pisec.security.AlarmManager;

import pisec.util.enums.AlarmState;
import pisec.util.enums.Zone;

import java.util.Date;

/**
 * Created by jamesrichardson on 9/21/15.
 */
public class DeviceManager {

    private static boolean initialized = false;

    /**
     * Register GPIO trigger devices here
     */
    public static void InitializeDevices(){
        if(initialized)
            return;

        GpioPinDigitalInput gpioPinDigitalInput_DOOR = GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_DOWN);
        GpioPinDigitalInput gpioPinDigitalInput_MOTION = GpioFactory.getInstance().provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);

        triggerDeviceOnStateForZone(gpioPinDigitalInput_DOOR, PinState.LOW, Zone.DOOR);
        triggerDeviceOnStateForZone(gpioPinDigitalInput_MOTION, PinState.HIGH, Zone.MOTION);

        initialized = true;
    }

    private static void triggerDeviceOnStateForZone(GpioPinDigitalInput device, PinState triggerState, Zone zone){
        device.addListener((GpioPinListenerDigital) event -> {
            if(((AlarmManager.GetSharedAlarmManager().getAlarmState() == AlarmState.ARMED) ||
               (AlarmManager.GetSharedAlarmManager().getAlarmState() == AlarmState.ALARMED)) &&
               event.getState() == triggerState)
            {
                AlarmManager.GetSharedAlarmManager().requestAlarm(zone.name() + " Triggered!", new Date(), zone);
            }
        });
    }
}

package pisec.util;

import pisec.util.enums.Setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by jamesrichardson on 9/16/15.
 */
public class Settings {
    private static Properties loadedProperties = null;
    private final static File propertiesLocation = new File(System.getProperty("user.home")+"/.pisec.conf");
    private static boolean loaded = false;
    private static final String debugPass = "789a";

    private void Settings(){}

    public static String getDebugPassword(){return debugPass;}

    public static String GetSettingForKey(Setting key){
        String ret = "";

        if(load() && !Flags.IsSet(Flags.TESTMODE)) {
            ret = loadedProperties.getProperty(key.toString());
        }
        if(Flags.IsSet(Flags.TESTMODE)){
            ret = getDebugProperties().getProperty(key.toString());
        }

        return ret;
    }

    public static void SetSettingForKey(String value, Setting key){
        if(load() && !Flags.IsSet(Flags.TESTMODE)) {
            loadedProperties.setProperty(key.toString(), value);
            if (key == Setting.PASSWORD) {
                Logger.Log("Modifying setting - Key:" + key.toString() + " Value:*****");
            } else {
                Logger.Log("Modifying setting - Key:" + key.toString() + " Value:" + value);
            }
            SaveSettings();
        }
    }

    public static void SaveSettings(){
        if(load() && !Flags.IsSet(Flags.TESTMODE)) {
            try {
                FileOutputStream outputStream = new FileOutputStream(propertiesLocation);
                loadedProperties.store(outputStream, null);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                Logger.Log(e.getMessage());
            }
            Logger.Log("Saved settings");
        }
    }

    private static boolean load(){
        if(loaded)
            return true;
        loadedProperties = new Properties();
        loadedProperties.setProperty(Setting.ALARM_ARM_TIME.toString(),"15");
        loadedProperties.setProperty(Setting.ALARM_DISARM_TIME.toString(),"15");
        loadedProperties.setProperty(Setting.PASSWORD.toString(),"");
        loadedProperties.setProperty(Setting.EMAIL_ADDR.toString(),"");
        loadedProperties.setProperty(Setting.FAIL_COOLDOWN.toString(),"10");
        loadedProperties.setProperty(Setting.LOG_LOCATION.toString(),"/home/pi/pisec.log");
        loadedProperties.setProperty(Setting.WIRELESS_DEVICE.toString(),"wlan0");
        loadedProperties.setProperty(Setting.SAVED_IMAGES_DIR.toString(),"/home/pi/pisec_images");
        loadedProperties.setProperty(Setting.PICTURES_PER_ALARM.toString(),"10");
        loadedProperties.setProperty(Setting.QUERY_SERVICE_ENABLE.toString(),"true");
        loadedProperties.setProperty(Setting.QUERY_SERVICE_PORT.toString(),"3579");

        FileInputStream inputStream;
        try {
            if(!propertiesLocation.exists()){
                FileOutputStream outputStream = new FileOutputStream(propertiesLocation);
                outputStream.flush();
                outputStream.close();
            }
            inputStream = new FileInputStream(propertiesLocation);
            loadedProperties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            Logger.Log("Unable to load properties! "+e.getMessage());
        }
        loaded = true;

        return loaded;
    }

    private static Properties getDebugProperties(){
        final Properties debugProperties = new Properties();
        debugProperties.setProperty(Setting.ALARM_ARM_TIME.toString(),"5");
        debugProperties.setProperty(Setting.ALARM_DISARM_TIME.toString(),"5");
        debugProperties.setProperty(Setting.PASSWORD.toString(),"ecab7a891401840d11a6bd64e21d24652814eb5735053034082481a264cc8b4b7a3f8fdce3830797b09c7e5fc2dde3d174be7e52d8de1e5e6c8d6a4b6eca31ed");
        debugProperties.setProperty(Setting.EMAIL_ADDR.toString(),"jamesrichardson2@gmail.com");
        debugProperties.setProperty(Setting.FAIL_COOLDOWN.toString(),"5");
        debugProperties.setProperty(Setting.LOG_LOCATION.toString(),"/home/pi/pisec.debug.log");
        debugProperties.setProperty(Setting.WIRELESS_DEVICE.toString(),"wlan0");
        debugProperties.setProperty(Setting.SAVED_IMAGES_DIR.toString(),"/home/pi/pisec_images");
        debugProperties.setProperty(Setting.PICTURES_PER_ALARM.toString(),"5");
        debugProperties.setProperty(Setting.QUERY_SERVICE_ENABLE.toString(),"true");
        debugProperties.setProperty(Setting.QUERY_SERVICE_PORT.toString(),"3579");

        return debugProperties;
    }
}

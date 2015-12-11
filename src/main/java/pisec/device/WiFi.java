package pisec.device;

import pisec.util.Logger;
import pisec.util.Settings;
import pisec.util.enums.Setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by jamesrichardson on 9/22/15.
 */
public class WiFi {

    public static int GetSignalStrength(){

        String ret = getIwconfigOutput();
        ret = ret.substring(ret.indexOf("Signal level=")+13,ret.indexOf("Signal level=")+16);
        String[] retSplit = ret.split("/");

        return Integer.valueOf(retSplit[0]);
    }

    private static String getIwconfigOutput(){
        StringBuffer output = new StringBuffer();

        try {
            Process p = Runtime.getRuntime().exec("iwconfig "+ Settings.GetSettingForKey(Setting.WIRELESS_DEVICE));
            p.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        }catch(Exception e){
            Logger.Log("Unable to get WiFi details! " + e.getMessage());
        }

        return output.toString();
    }
}

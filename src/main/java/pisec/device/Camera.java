package pisec.device;

import pisec.util.Flags;
import pisec.util.Logger;
import pisec.util.Settings;
import pisec.util.enums.Setting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jamesrichardson on 9/22/15.
 */
public class Camera {
    private final static File imageSaveDir = new File(Settings.GetSettingForKey(Setting.SAVED_IMAGES_DIR));
    private static String tmpSaveLocation = "";

    public static String TakePicture(){

        if(!Flags.IsSet(Flags.ONRASPI))
            return "";

        if(!imageSaveDir.exists())
            imageSaveDir.mkdir();

        tmpSaveLocation = Settings.GetSettingForKey(Setting.SAVED_IMAGES_DIR)+"/"+new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss").format(new Date());

        if(!new File(tmpSaveLocation).exists())
            new File(tmpSaveLocation).mkdir();

        for(int i = 0;i< Integer.valueOf(Settings.GetSettingForKey(Setting.PICTURES_PER_ALARM));i++){
            try {
                Process p = Runtime.getRuntime().exec("raspistill -o "+tmpSaveLocation+"/image_"+i+".jpg");
                BufferedInputStream bis = new BufferedInputStream(p.getInputStream());
                FileOutputStream fos = new FileOutputStream(tmpSaveLocation+"/image_"+i+"+_log.log");

                p.waitFor();

                int read = bis.read();
                fos.write(read);
                while (read != -1) {
                    read = bis.read();
                    fos.write(read);
                }

                bis.close();
                fos.close();
            } catch (IOException ieo) {
                Logger.Log("Unable to take picture! " + ieo.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return tmpSaveLocation;
    }
}

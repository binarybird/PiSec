package pisec.util;

import jline.console.ConsoleReader;
import pisec.gui.concurrency.CLIMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jamesrichardson on 9/21/15.
 */
public class Logger {
    private final static File logLocation = new File(System.getProperty("user.home")+"/pisec.log");//todo - Settings.GetSetting(Enums.Setting.LOG_LOCATION);

    private static CLIMonitor monitor = null;

    public static void SetCLIMonitor(CLIMonitor m){
        monitor = m;
    }

    public static void Log(String message){Log(false,message);}
    public static void Log(boolean silent,String message){

        message.replaceAll("\\n","");

        FileOutputStream logOutputStream = null;
        FileInputStream inputStream;
        String logContents = null;
        byte[] data = null;

        try {
            if(!logLocation.exists()){
                FileOutputStream outputStream = new FileOutputStream(logLocation);
                outputStream.flush();
                outputStream.close();
            }
            inputStream = new FileInputStream(logLocation);

            data = new byte[(int) logLocation.length()];
            inputStream.read(data);
            inputStream.close();

            logContents = new String(data, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(logContents == null) {
            System.out.println("Unable to read log file!");
            return;
        }

        String newMessage = "["+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+"] "+message;
        if(!silent) {
            if(monitor != null){
                monitor.printToInteractiveConsole(newMessage);
            }else{
                System.out.println(newMessage);
            }
        }
        logContents+=newMessage;

        try{
            logOutputStream = new FileOutputStream(logLocation);
            logOutputStream.write(logContents.getBytes());
            logOutputStream.flush();
            logOutputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

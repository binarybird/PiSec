package pisec.gui.concurrency;

import javafx.application.Platform;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.apache.commons.lang3.StringUtils;
import pisec.Main;
import pisec.gui.control.UIMessage;
import pisec.gui.controller.CodeInputPaneController;
import pisec.gui.controller.RootPaneController;
import pisec.io.WebService;
import pisec.io.XMLSerializer;
import pisec.security.AlarmManager;
import pisec.security.auth.AuthValidator;
import pisec.security.auth.Token;
import pisec.util.Logger;
import pisec.util.enums.AlarmMode;
import pisec.util.enums.AlarmState;
import pisec.util.enums.Zone;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jamesrichardson on 10/16/15.
 */
public class CLIMonitor extends Thread{

    private static boolean resetPasswordFlag = false;
    public static boolean GetResetPassFlag(){return resetPasswordFlag;}
    private static final ArrayList<String> help = new ArrayList<>();
    private static final String prompt = "PiSec> ";
    private static boolean interrupt = false;
    private ConsoleReader console = null;

    public CLIMonitor(){
        Logger.Log(CLIMonitor.class.getName()+"[Starting interactive Console]");
    }

    @Override
    public void interrupt(){
//        if(console != null)
//            console.shutdown();
        interrupt = true;
        super.interrupt();
    }

    public void printToInteractiveConsole(String s){
        try {
            console.println(s);
            console.flush();
            console.getOutput().flush();

        } catch (IOException e) {

        }
    }

    @Override
    public void run(){
        help.addAll(Arrays.stream(new String[] {"stop", "help", "status", "listalarms", "purgealarms", "resetpass", "test", "clear", "disarm", "arm (stay,instant)", "serialize (location)", "webservice (start,stop)"}).collect(Collectors.toList()));
        String line = "";

        try {
            console = new ConsoleReader();
            List<Completer> completors = new LinkedList<>();
            completors.add(new StringsCompleter(help));
            console.addCompleter(new ArgumentCompleter(completors));
            help();
            console.println();
            while ((line = console.readLine(prompt)) != null) {

                if(isInterrupted() || interrupt){
                    Logger.Log(prompt+"Console interrupt!");
                    break;
                }

                if(line.equalsIgnoreCase("stop")) {
                    if (AlarmManager.GetSharedAlarmManager().getAlarmState() != AlarmState.DISARMED) {
                        line = "";
                        Logger.Log(prompt+"Alarm must be disarmed before shutting down!");
                    }else{
                        console.shutdown();
                        Main.ShutDown();
                    }
                }

                if(line.equalsIgnoreCase("help")){
                    help();
                }

                if(line.equalsIgnoreCase("status")){
                    status();
                }

                if(line.equalsIgnoreCase("listalarms")){
                    alarms();
                }

                if(line.equalsIgnoreCase("purgealarms")){
                    purgeAlarms(console);
                }

                if(line.equalsIgnoreCase("test")){
                    test();
                }

                if(line.equalsIgnoreCase("disarm")){
                    disarm(console);
                }else if(StringUtils.containsIgnoreCase(line, "arm")){
                    arm(line,console);
                }

                if(line.equalsIgnoreCase("resetpass")){
                    resetPassword(console);
                }

                if(line.equalsIgnoreCase("clear")){
                    console.clearScreen();
                }

                if(StringUtils.containsIgnoreCase(line, "webservice")){
                    webservice(line);
                }

                if(StringUtils.containsIgnoreCase(line, "serialize")){
                    serialize(line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(!isInterrupted())
                    TerminalFactory.get().restore();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void help(){
        String h = "";
        int i=0;
        for(String e : help){
            if(i==0) {
                h += e;
            }else{
                h +=", "+e;
            }
            i++;
        }

        Logger.Log(prompt+"Available Commands:\n"+h);
    }

    private static void purgeAlarms(ConsoleReader console) throws IOException{
        final Token token = getToken(console);
        final boolean b = AlarmManager.GetSharedAlarmManager().purgeAlarms(token);
        if(b) {
            console.println("Alarms purged");
        }else{
            console.println("Alarms not purged");
        }
    }

    private static void webservice(String line){
        try {
            final String[] split = line.split(" ");
            if (split.length == 2) {
                final String startstop = split[1];

                if (startstop != null) {
                    if(StringUtils.containsIgnoreCase(startstop, "start")){
                        WebService.StartWebService();
                    }else if(StringUtils.containsIgnoreCase(startstop, "stop")){
                        WebService.StopWebService();
                    }
                }
            }
        }catch(Exception e){
            Logger.Log(prompt+e.getMessage());
        }
    }

    private static void serialize(String line){
        try {
            final String[] split = line.split(" ");
            if (split.length == 2) {
                final String location = split[1];

                if (location != null) {
                    new File(location.substring(0,location.lastIndexOf(File.separator))).mkdirs();
                    Files.write(Paths.get(new URI("file:"+location)),XMLSerializer.Serialize().getBytes());
                }
            }
        }catch(Exception e){
            Logger.Log(prompt+e.getMessage());
        }
    }

    private static void status(){
        Logger.Log(prompt + AlarmManager.GetSharedAlarmManager().toString());
        Logger.Log(prompt + RootPaneController.GetSharedRootPaneController().toString());
        Logger.Log(prompt + WebService.GetServiceStatus());
    }

    private static void alarms(){
        if(AlarmManager.GetSharedAlarmManager().getPreviousAlarms().size() == 0){
            Logger.Log(prompt+"No alarms triggered");
        }else {
            for (int i=0;i<AlarmManager.GetSharedAlarmManager().getPreviousAlarms().size();i++){
                Logger.Log(prompt+"(" + i + ") " + AlarmManager.GetSharedAlarmManager().getPreviousAlarms().get(i).toString());
            }
        }
    }

    private static void test(){
        AlarmManager.GetSharedAlarmManager().requestAlarm("Test Triggered!", new Date(), Zone.TEST);
    }

    private static void disarm(ConsoleReader console) throws IOException{
        final Token token = getToken(console);
        if (token != null)
            AlarmManager.GetSharedAlarmManager().requestDisarm(token);
    }

    private static void arm(String line, ConsoleReader console) throws IOException{
        try {
            final String[] split = line.split(" ");
            if (split.length == 2) {
                final String mode = split[1];

                if (mode != null) {
                    final AlarmMode alarmMode = AlarmMode.valueOf(mode.toUpperCase());
                    final Token token = getToken(console);

                    if (alarmMode != null && token != null) {
                        switch (alarmMode) {
                            case STAY:AlarmManager.GetSharedAlarmManager().requestArm_Stay(token);break;
                            case INSTANT:AlarmManager.GetSharedAlarmManager().requestArm_Instant(token);break;
                            case NOTSET:break;
                            default:break;
                        }
                    }
                }
            }
        }catch(Exception e){
            Logger.Log(prompt+e.getMessage());
        }
    }

    private static void resetPassword(ConsoleReader console) throws IOException{
        final String in = console.readLine("Are You Sure? (y,n)> ");

        if(in.equalsIgnoreCase("y")) {
            if (AlarmManager.GetSharedAlarmManager().getAlarmState() == AlarmState.DISARMED) {
                Logger.Log("Please see the alarm screen/GUI to enter a new password!");
                resetPasswordFlag = true;
                Platform.runLater(() -> {
                    RootPaneController.GetSharedRootPaneController().push(new CodeInputPaneController(action -> {
                        resetPasswordFlag = false;
                    }));
                    RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Enter New Password"), true);
                });
            } else {
                RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Must disarm to reset pass!"), true);
                Logger.Log(prompt + "Alarm must be disarmed before resetting the password!");
            }
        }
    }

    private static Token getToken(ConsoleReader console) throws IOException{

        final String passwordString  = console.readLine("EnterPassword> ",'*');

        return AuthValidator.generateAuthToken(passwordString.toUpperCase());
    }
}

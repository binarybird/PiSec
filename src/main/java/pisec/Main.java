package pisec;

import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import pisec.device.DeviceManager;
import pisec.gui.concurrency.CLIMonitor;
import pisec.gui.concurrency.TimerManager;
import pisec.gui.controller.RootPaneController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pisec.gui.event.alarm.DisarmEvent;
import pisec.io.WebService;

import pisec.util.Flags;
import pisec.util.Logger;
import pisec.util.Settings;
import pisec.util.enums.Setting;


/**
 * Created by jamesrichardson on 10/14/15.
 *
 * Singltons that run the show:
 *
 * pisec.security.AlarmManager
 * pisec.gui.controller.RootPaneController
 * pisec.gui.concurrency.TimerManager
 *
 */
public class Main extends Application{

    public static void main(String[] args) {
        Logger.Log("Starting PiSec");
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(Flags.IsSet(Flags.DEBUG))
            primaryStage.addEventHandler(Event.ANY, Main::debugEvents);

        final RootPaneController rootController = RootPaneController.GetSharedRootPaneController();
        rootController.getHomePaneController().fireEvent(new DisarmEvent("Disarmed"));

        if (Boolean.valueOf(Settings.GetSettingForKey(Setting.QUERY_SERVICE_ENABLE))) {
            WebService.StartWebService();
        }

        final CLIMonitor monitor = new CLIMonitor();
        monitor.start();
        Logger.SetCLIMonitor(monitor);

        final Scene scene = new Scene(rootController);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            monitor.interrupt();
            ShutDown();
        });

        if(System.getProperty("os.name").equals("Linux") && System.getProperty("os.arch").equals("arm")) {
            Flags.SetFlag(Flags.ONRASPI);//just a guess
            primaryStage.setFullScreen(true);
            DeviceManager.InitializeDevices();
        }
    }
    public static void ShutDown(){
        Logger.SetCLIMonitor(null);
        Logger.Log("[ShutDown]");
        WebService.StopWebService();
        TimerManager.GetSharedTimerManager().CancleAllTimers();
        Settings.SaveSettings();
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }
    static final KeyCombination shutdownShortcut = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
    static private void debugEvents(Event event){
        System.out.println("[DEBUG] Type:"+event.getEventType().getName()+" Source:"+event.getSource().getClass()+" Target:"+event.getTarget().getClass()+" Event:"+event);
    }
}
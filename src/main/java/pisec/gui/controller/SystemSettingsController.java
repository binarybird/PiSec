package pisec.gui.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import pisec.gui.control.UIController;
import pisec.gui.event.alarm.*;
import pisec.gui.event.ui.ApplyEvent;
import pisec.gui.event.ui.CancelEvent;
import pisec.gui.event.ui.CloseEvent;
import pisec.util.Flags;
import pisec.util.Logger;
import pisec.util.Settings;
import pisec.util.enums.Setting;

import java.io.IOException;


/**
 * Created by jamesrichardson on 9/16/15.
 */
public class SystemSettingsController extends UIController {
    private boolean logLocModified = false;
    private boolean emailModified = false;
    private boolean wifiModified = false;
    private boolean imageSavedirModified = false;
    private boolean disarmTimeTextFieldModified = false;
    private boolean armTimeTextfieldModified = false;

    public SystemSettingsController(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SystemSettingsPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException(exception);
        }
    }

    //TODO reset password

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField logLocationTextField;

    @FXML
    private TextField wifiDeviceTextField;

    @FXML
    private TextField imageSavedirTextField;

    @FXML
    private TextField disarmTimeTextField;

    @FXML
    private TextField armTimeTextfield;

    @FXML
    private Button restartButton;

    @FXML
    private Button shutdownButton;

    @FXML
    private Button passwordResetButton;

    @FXML
    private void initialize(){
        emailTextField.setText(Settings.GetSettingForKey(Setting.EMAIL_ADDR));
        logLocationTextField.setText(Settings.GetSettingForKey(Setting.LOG_LOCATION));
        wifiDeviceTextField.setText(Settings.GetSettingForKey(Setting.WIRELESS_DEVICE));
        imageSavedirTextField.setText(Settings.GetSettingForKey(Setting.SAVED_IMAGES_DIR));
        disarmTimeTextField.setText(Settings.GetSettingForKey(Setting.ALARM_DISARM_TIME));
        armTimeTextfield.setText(Settings.GetSettingForKey(Setting.ALARM_ARM_TIME));

        emailTextField.textProperty().addListener((l,o,n)-> emailModified = true);
        logLocationTextField.textProperty().addListener((l,o,n)-> logLocModified = true);
        wifiDeviceTextField.textProperty().addListener((l,o,n)-> wifiModified = true);
        imageSavedirTextField.textProperty().addListener((l,o,n)-> imageSavedirModified = true);
        disarmTimeTextField.textProperty().addListener((l,o,n)-> disarmTimeTextFieldModified = true);
        armTimeTextfield.textProperty().addListener((l,o,n)-> armTimeTextfieldModified = true);
    }

    @FXML
    private void handleRestartButton(ActionEvent event) {
        Logger.Log("Restarting Raspberry Pi...");
        if(!Flags.IsSet(Flags.ONRASPI))
            return;
        StringBuffer output = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec("sudo reboot");
            p.waitFor();

        } catch (Exception e) {
            Logger.Log("Unable to restart Raspberry Pi! "+e.getMessage());
        }
    }

    @FXML
    private void handleShutdownButton(ActionEvent event) {
        Logger.Log("Shutting down Raspberry Pi...");
        if(!Flags.IsSet(Flags.ONRASPI))
            return;
        StringBuffer output = new StringBuffer();
        try {
            Process p = Runtime.getRuntime().exec("sudo shutdown -h now");
            p.waitFor();

        } catch (Exception e) {
            Logger.Log("Unable to shutdown Raspberry Pi! "+e.getMessage());
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event){
        cleanUp();
        RootPaneController.GetSharedRootPaneController().pop();
    }

    private void cleanUp(){
        if(logLocModified){
            Settings.SetSettingForKey(logLocationTextField.getText(), Setting.LOG_LOCATION);
        }
        if(emailModified){
            Settings.SetSettingForKey(emailTextField.getText(), Setting.EMAIL_ADDR);
        }
        if(wifiModified){
            Settings.SetSettingForKey(wifiDeviceTextField.getText(), Setting.WIRELESS_DEVICE);
        }
        if(imageSavedirModified){
            Settings.SetSettingForKey(imageSavedirTextField.getText(), Setting.SAVED_IMAGES_DIR);
        }
        if(armTimeTextfieldModified){
            Settings.SetSettingForKey(armTimeTextfield.getText(), Setting.ALARM_ARM_TIME);
        }
        if(disarmTimeTextFieldModified){
            Settings.SetSettingForKey(disarmTimeTextField.getText(), Setting.ALARM_DISARM_TIME);
        }

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
    public void handleArmEvent(ArmEvent event) {

    }

    @Override
    public void handleArmingEvent(ArmingEvent event) {

    }

    @Override
    public void handleDisarmEvent(DisarmEvent event) {

    }

    @Override
    public void handleDisarmingEvent(DisarmingEvent event) {

    }

    @Override
    public void handleAlarmEvent(AlarmEvent event) {

    }
}

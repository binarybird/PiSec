package pisec.gui.controller;

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import pisec.Main;
import pisec.device.WiFi;
import pisec.gui.concurrency.TimerManager;
import pisec.gui.control.UIController;
import pisec.gui.control.UIMessage;
import pisec.gui.event.alarm.*;
import pisec.gui.event.ui.ApplyEvent;
import pisec.gui.event.ui.CancelEvent;
import pisec.gui.event.ui.CloseEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import pisec.security.AlarmManager;
import pisec.security.auth.AuthRequest;
import pisec.security.auth.AuthValidator;
import pisec.util.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jamesrichardson on 10/14/15.
 */
public class RootPaneController extends UIController{
    @FXML
    private StackPane rootPaneStack;
    @FXML
    private Label messageLabel;
    @FXML
    private ImageView wifiImage;

    private Queue<UIMessage> messageQueue = new LinkedBlockingQueue<>();
    private UIMessage currentUIMessage = null;
    private FadeTransition fadeOut = new FadeTransition(Duration.millis(1000));
    private static RootPaneController rootPaneController = null;

    public static RootPaneController GetSharedRootPaneController(){

        if(rootPaneController == null){
            rootPaneController = new RootPaneController();
        }
        return rootPaneController;
    }

    private RootPaneController(){
        super();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RootPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException(exception);
        }
        if(rootPaneController == null)
            rootPaneController = this;
    }

    public void popToHomeScreen(){
        ArrayList<Node> toRemove = new ArrayList<>();
        for(int i=0; i<rootPaneStack.getChildren().size(); i++){
            if(i==0)continue;
            toRemove.add(rootPaneStack.getChildren().get(i));
        }
        rootPaneStack.getChildren().removeAll(toRemove);
    }
    public void pop(){
        if(rootPaneStack.getChildren().size() > 1)
            rootPaneStack.getChildren().remove(rootPaneStack.getChildren().size()-1);
    }
    public void pop(UIController controller){
        rootPaneStack.getChildren().remove(controller);
    }
    public void push(UIController controller){
        rootPaneStack.getChildren().add(controller);
    }
    public UIController getTopMostPane(){
        return (UIController) rootPaneStack.getChildren().get(rootPaneStack.getChildren().size()-1);
    }

    public HomePaneController getHomePaneController(){
        HomePaneController ret = null;
        final Node node = rootPaneStack.getChildren().get(0);
        if(node instanceof HomePaneController){
            ret = (HomePaneController) node;
        }

        return ret;
    }

    private void fadeMessage(){
        fadeOut.setNode(messageLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setCycleCount(1);
        fadeOut.setOnFinished(event -> {
            messageLabel.setText("");
            messageLabel.setOpacity(1.0);
            messageLabel.setVisible(true);

        });
        fadeOut.setAutoReverse(false);
        fadeOut.playFromStart();
    }

    public void setMessage(UIMessage message, boolean forceOverwriteCurrentMessage){
        if(forceOverwriteCurrentMessage) {
            if (currentUIMessage != null) {
                currentUIMessage.cancelTimer();
            }
            currentUIMessage = message;
            Platform.runLater(() -> {
                messageLabel.setText(currentUIMessage.getMessage());
            });
            currentUIMessage.startTimer(onEnd -> {
                Platform.runLater(this::fadeMessage);
                currentUIMessage = null;
                processMessage();
            });
        }else{
            setMessage(message);
        }
    }

    public void setMessage(UIMessage message){
        messageQueue.add(message);
        processMessage();
    }

    private void processMessage(){
        if (currentUIMessage == null && messageQueue.size() > 0) {
            currentUIMessage = messageQueue.poll();
            Platform.runLater(()->{messageLabel.setText(currentUIMessage.getMessage());});
            currentUIMessage.startTimer(onEnd -> {
                Platform.runLater(this::fadeMessage);
                currentUIMessage = null;
                processMessage();
            });
        }
    }

    @FXML
    private void initialize(){
        setMessage(new UIMessage("Welcome!"));
        updateWifiImage();
        TimerManager.GetSharedTimerManager().createSchedualedTask(60000,action->Platform.runLater(this::updateWifiImage));
        HomePaneController hpc = new HomePaneController();
        push(hpc);
    }

    @FXML
    void handleDisarmButton(ActionEvent event) {
        CodeInputPaneController codeInput = new CodeInputPaneController(AlarmManager.GetSharedAlarmManager()::requestDisarm);
        push(codeInput);
    }

    @FXML
    void handleInstantButton(ActionEvent event) {
        CodeInputPaneController codeInput = new CodeInputPaneController(AlarmManager.GetSharedAlarmManager()::requestArm_Instant);
        push(codeInput);
    }

    @FXML
    void handleSettingsButton(ActionEvent event) {
        CodeInputPaneController codeInput = new CodeInputPaneController(token->{
            AuthRequest req = new AuthRequest(token,action->{
                SystemSettingsController sysSettings = new SystemSettingsController();
                push(sysSettings);
            });
            AuthValidator.performRequest(req);
        });
        push(codeInput);
    }

    @FXML
    void handleStayButton(ActionEvent event) {
        CodeInputPaneController codeInput = new CodeInputPaneController(AlarmManager.GetSharedAlarmManager()::requestArm_Stay);
        push(codeInput);
    }

    private void updateWifiImage(){
        int signalStrength = -1;

        final String sysName = System.getProperty("os.name");
        if(!sysName.equals("Mac OS X") && !sysName.toUpperCase().contains("WINDOWS")) {
            try {
                signalStrength = WiFi.GetSignalStrength();
            }catch(Exception e){
                Logger.Log("Unable to get WiFi status!");
            }
        }

        if(signalStrength < 0 || signalStrength > 100){
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_not_connected.png")));
            return;
        }

        if(signalStrength>=0 && signalStrength <= 10){
            //Bar * 0
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_a1.png")));
        }else if(signalStrength > 10 && signalStrength <= 20){
            //Bar * 1
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_a2.png")));
        }else if(signalStrength > 20 && signalStrength <= 40){
            //Bar * 2
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_a3.png")));
        }else if(signalStrength > 40 && signalStrength <= 60){
            //Bar * 3
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_a4.png")));
        }else if(signalStrength > 60 && signalStrength <= 80){
            //Bar * 4
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_d5.png")));
        }else if(signalStrength > 80 ){
            //Bar * 5
            wifiImage.setImage(new Image(Main.class.getResourceAsStream("gui/images/wifi_a6.png")));
        }
    }

    @Override
    public void handleCloseEvent(CloseEvent<?> event) {}

    @Override
    public void handleApplyEvent(ApplyEvent<?> event) {}

    @Override
    public void handleCancelEvent(CancelEvent<?> event) {}


    @Override
    public void handleArmEvent(ArmEvent event) {
        setMessage(new UIMessage(event.getMessage()));
    }

    @Override
    public void handleArmingEvent(ArmingEvent event) {
        setMessage(new UIMessage(event.getMessage()));
    }

    @Override
    public void handleDisarmEvent(DisarmEvent event) {
        setMessage(new UIMessage(event.getMessage()));
    }

    @Override
    public void handleDisarmingEvent(DisarmingEvent event) {
        setMessage(new UIMessage(event.getMessage()));
    }

    @Override
    public void handleAlarmEvent(AlarmEvent event) {
        setMessage(new UIMessage(event.getMessage()));
    }

    public int getOpenWindows(){
        return rootPaneStack.getChildren().size();
    }
    public String getCurrentMessage(){
        return messageLabel.getText();
    }

    public String toString(String message){
        return RootPaneController.class.getName()+"[OpenWindows:\""+rootPaneStack.getChildren().size()+"\" Message:\""+messageLabel.getText()+"\"]";
    }

    @Override
    public String toString(){
        return this.toString("");
    }

}

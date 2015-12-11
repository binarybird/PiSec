package pisec.gui.controller;


import pisec.gui.concurrency.CLIMonitor;
import pisec.gui.concurrency.TimerManager;
import pisec.gui.control.UIController;
import pisec.gui.control.UIMessage;
import pisec.gui.event.alarm.*;
import pisec.gui.event.ui.ApplyEvent;
import pisec.gui.event.ui.CancelEvent;
import pisec.gui.event.ui.CloseEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import pisec.security.auth.AuthValidator;
import pisec.security.auth.Token;
import pisec.util.Logger;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Created by jamesrichardson on 9/16/15.
 */
public class CodeInputPaneController extends UIController {

    private Consumer<Token> actionOnEnter;

    public CodeInputPaneController(Consumer<Token> actionOnEnter){
        super();
        this.actionOnEnter = actionOnEnter;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CodeInputPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException(exception);
        }

        if(AuthValidator.getCooldown()){
            lock();
        }

    }

    private String buildCodeString = "";
    @FXML
    private Button buttonFour;
    @FXML
    private Button buttonEight;
    @FXML
    private Button buttonNine;
    @FXML
    private Button buttonA;
    @FXML
    private Button buttonFive;
    @FXML
    private Button buttonSeven;
    @FXML
    private Button buttonZero;
    @FXML
    private Button buttonOne;
    @FXML
    private Button buttonTwo;
    @FXML
    private Button backButton;
    @FXML
    private Button buttonSix;
    @FXML
    private Button buttonB;
    @FXML
    private Button buttonThree;
    @FXML
    private Button buttonC;
    @FXML
    private Button buttonEnter;
    @FXML
    private Button buttonD;

    public void flashRed(){

        setButtonColor("-fx-background-color: red;");

        TimerManager.GetSharedTimerManager().createCountUp(1000,action-> setButtonColor("-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"));

    }

    private void setButtonColor(String value){
        buttonFour.setStyle(value);
        buttonEight.setStyle(value);
        buttonNine.setStyle(value);
        buttonA.setStyle(value);
        buttonFive.setStyle(value);
        buttonSeven.setStyle(value);
        buttonZero.setStyle(value);
        buttonOne.setStyle(value);
        buttonTwo.setStyle(value);
        backButton.setStyle(value);
        buttonSix.setStyle(value);
        buttonB.setStyle(value);
        buttonThree.setStyle(value);
        buttonC.setStyle(value);
        buttonEnter.setStyle(value);
        buttonD.setStyle(value);
    }


    private String passOne = null;
    private boolean secondInput = false;
    private String password = null;
    public String getPassword(){
        return password;
    }
    @FXML
    private void handleBackButton(ActionEvent event){
        if(secondInput || CLIMonitor.GetResetPassFlag()){
            actionOnEnter.accept(Token.GenerateToken());
            RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Password Reset Canceled!"),true);
            RootPaneController.GetSharedRootPaneController().pop(this);
        }
        fireEvent(new CancelEvent<CodeInputPaneController>(this,""));
    }

    @FXML
    private void handleEnter(ActionEvent event) {
        final Token token = AuthValidator.generateAuthToken(buildCodeString);

        if(CLIMonitor.GetResetPassFlag()){
            RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Enter Password Again"),true);

            if(secondInput){
                actionOnEnter.accept(token);
                if(passOne.equals(buildCodeString)){
                    secondInput = false;
                    this.password = buildCodeString;
                    AuthValidator.passwordReset();
                    RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Password Reset!"),true);
                }else{
                    RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Passwords do not match!"),true);
                }
                RootPaneController.GetSharedRootPaneController().pop(this);
            }else{
                passOne = buildCodeString;
                buildCodeString = "";
            }
            secondInput = true;
        }else{
            actionOnEnter.accept(token);
            buildCodeString = "";

            RootPaneController.GetSharedRootPaneController().pop(this);
        }

    }

    @FXML
    private void initialize(){

    }

    @FXML
    private void handleA(ActionEvent event) {
        buildCodeString+="A";
    }

    @FXML
    private void handleB(ActionEvent event) {
        buildCodeString+="B";
    }

    @FXML
    private void handleC(ActionEvent event) {
        buildCodeString+="C";
    }

    @FXML
    private void handleD(ActionEvent event) {
        buildCodeString+="D";
    }

    @FXML
    private void handleEight(ActionEvent event) {
        buildCodeString+="8";
    }

    @FXML
    private void handleFive(ActionEvent event) {
        buildCodeString+="5";
    }

    @FXML
    private void handleFour(ActionEvent event) {
        buildCodeString+="4";
    }

    @FXML
    private void handleNine(ActionEvent event) {
        buildCodeString+="9";
    }

    @FXML
    private void handleOne(ActionEvent event) {
        buildCodeString+="1";
    }

    @FXML
    private void handleSeven(ActionEvent event) {
        buildCodeString+="7";
    }

    @FXML
    private void handleSix(ActionEvent event) {
        buildCodeString+="6";
    }

    @FXML
    private void handleThree(ActionEvent event) {
        buildCodeString+="3";
    }

    @FXML
    private void handleTwo(ActionEvent event) {
        buildCodeString+="2";
    }

    @FXML
    private void handleZero(ActionEvent event) {
        buildCodeString+="0";
    }

    private void lock() {
        buttonFour.setDisable(true);
        buttonEight.setDisable(true);
        buttonNine.setDisable(true);
        buttonA.setDisable(true);
        buttonFive.setDisable(true);
        buttonSeven.setDisable(true);
        buttonZero.setDisable(true);
        buttonOne.setDisable(true);
        buttonTwo.setDisable(true);
        buttonSix.setDisable(true);
        buttonB.setDisable(true);
        buttonThree.setDisable(true);
        buttonC.setDisable(true);
        buttonEnter.setDisable(true);
        buttonD.setDisable(true);
    }

    private void unLock() {
        buttonFour.setDisable(false);
        buttonEight.setDisable(false);
        buttonNine.setDisable(false);
        buttonA.setDisable(false);
        buttonFive.setDisable(false);
        buttonSeven.setDisable(false);
        buttonZero.setDisable(false);
        buttonOne.setDisable(false);
        buttonTwo.setDisable(false);
        backButton.setDisable(false);
        buttonSix.setDisable(false);
        buttonB.setDisable(false);
        buttonThree.setDisable(false);
        buttonC.setDisable(false);
        buttonEnter.setDisable(false);
        buttonD.setDisable(false);
    }

    @Override
    public void handleCloseEvent(CloseEvent<?> event) {

    }

    @Override
    public void handleApplyEvent(ApplyEvent<?> event) {

    }

    @Override
    public void handleCancelEvent(CancelEvent<?> event) {
        event.consume();
        RootPaneController.GetSharedRootPaneController().pop(this);
    }

    @Override
    public void handleArmEvent(ArmEvent event) {
        Logger.Log("[CodeInputPane] Arm Event: "+event.getMessage());
    }

    @Override
    public void handleArmingEvent(ArmingEvent event) {
        Logger.Log("[CodeInputPane] Arming Event: "+event.getMessage());
    }

    @Override
    public void handleDisarmEvent(DisarmEvent event) {
        Logger.Log("[CodeInputPane] Disarm Event: "+event.getMessage());
    }

    @Override
    public void handleDisarmingEvent(DisarmingEvent event) {
        Logger.Log("[CodeInputPane] Disarming Event: "+event.getMessage());
    }

    @Override
    public void handleAlarmEvent(AlarmEvent event) {
        Logger.Log("[CodeInputPane] Alarm Event: "+event.getMessage());
    }
}

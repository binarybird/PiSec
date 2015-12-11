package pisec.security.auth;

import pisec.gui.concurrency.TimerManager;
import pisec.gui.control.UIController;
import pisec.gui.control.UIMessage;
import pisec.gui.controller.CodeInputPaneController;
import pisec.gui.controller.RootPaneController;
import pisec.util.Flags;
import pisec.util.Logger;
import pisec.util.Settings;
import pisec.util.enums.Setting;

import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * Created by binar on 10/16/2015.
 */
public class AuthValidator {

    private static long lastPasswordEntryTime = 0;
    private static int failedLoginAttempts = 0;
    private static int failedLoginAttemptsTotal = 0;
    private static boolean cooldown = false;

    public static Token generateAuthToken(String password){
        return InternalValidator.GenerateAuthToken(password);
    }

    public static boolean performRequest(AuthRequest authRequest){
        final boolean ret = InternalValidator.PerformRequest(authRequest);
        cooldownCheck(ret);
        return ret;
    }

    public static boolean getCooldown(){return cooldown;}

    public static int getFailedLoginAttemptsTotal(){return failedLoginAttemptsTotal;}

    private static boolean cooldownCheck(boolean attemptResult){
        if(Flags.IsSet(Flags.TESTMODE))
            return false;

        if(cooldown)
            return true;

        boolean ret = false;

        final int failCooldown = Integer.valueOf(Settings.GetSettingForKey(Setting.FAIL_COOLDOWN));

        if(!attemptResult){
            failedLoginAttempts++;
            failedLoginAttemptsTotal++;
            if(failedLoginAttempts > 3){
                failedLoginAttempts = 0;
                ret = true;
            }
            Logger.Log(AuthValidator.class.getName()+"[TotalFailedLogins:\""+failedLoginAttemptsTotal+"\" FailedLoginSequence:\""+failedLoginAttempts+"\" CoolDown:\""+cooldown+"\" LastAttemptInterval:\""+(System.currentTimeMillis() - lastPasswordEntryTime)*0.001+"\"");

        }

        if(lastPasswordEntryTime == 0){
            lastPasswordEntryTime = System.currentTimeMillis();
        }else if((System.currentTimeMillis() - lastPasswordEntryTime)*0.001 < 5){
            Logger.Log(AuthValidator.class.getName()+"[FailedLogins:\""+failedLoginAttempts+"\" CoolDown:\""+cooldown+"\" LastAttemptInterval:\""+(System.currentTimeMillis() - lastPasswordEntryTime)*0.001+"\"");
            ret = true;
        }

        if(ret){
            cooldown=true;
            Logger.Log(AuthValidator.class.getName()+"[FailedLogins:\""+failedLoginAttempts+"\" CoolDown:\""+cooldown+"\" LastAttemptInterval:\""+(System.currentTimeMillis() - lastPasswordEntryTime)*0.001+"\"");
            TimerManager.GetSharedTimerManager().createCountDown(failCooldown,intin->{
                RootPaneController.GetSharedRootPaneController().setMessage(new UIMessage("Auth Fail Cool down - "+intin),true);
                if(intin <= 0) {
                    cooldown = false;
                }
            });

        }

        return ret;

    }

    public static void passwordReset(){
        final UIController topmostPane = RootPaneController.GetSharedRootPaneController().getTopMostPane();
        if(topmostPane != null && topmostPane instanceof CodeInputPaneController) {
            String pass = ((CodeInputPaneController) topmostPane).getPassword();
            if(pass != null) {
                InternalValidator.SetNewPassword(pass);
            }
        }
    }

    private static final class InternalValidator {

        private InternalValidator(){}

        private static final ArrayList<Token> validTokens = new ArrayList<>();

        public static boolean PerformRequest(AuthRequest authRequest){
            boolean performedAction = false;
            if(validTokens.remove(authRequest.getToken())){
                authRequest.getRequest().accept(null);
                performedAction = true;
            }
            return performedAction;
        }

        public static Token GenerateAuthToken(final String password) {
            Token token = Token.GenerateToken();

            final String passhash = Settings.GetSettingForKey(Setting.PASSWORD);

            try {
                if(passhash.equals(hashText(password))){
                    validTokens.add(token);
                }
            } catch (Exception e) {
                Logger.Log("Unable to check password! " + e.getMessage());
            }

            return token;
        }

        public static void SetNewPassword(final String newPassword) {

            try {
                Settings.SetSettingForKey(hashText(newPassword), Setting.PASSWORD);
            } catch (Exception e) {
                Logger.Log("Unable to set new password! " + e.getMessage());
            }

        }

        private static String convertByteToHex(byte data[])
        {
            StringBuffer hexData = new StringBuffer();
            for (int byteIndex = 0; byteIndex < data.length; byteIndex++)
                hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));

            return hexData.toString();
        }

        private static String hashText(String textToHash) throws Exception
        {
            final MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            sha512.update(textToHash.getBytes());

            return convertByteToHex(sha512.digest());
        }
    }
}

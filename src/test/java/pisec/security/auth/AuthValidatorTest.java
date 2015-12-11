package pisec.security.auth;

import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import pisec.security.AlarmManager;
import pisec.util.Flags;
import pisec.util.Settings;
import pisec.util.enums.Setting;

/**
 * Created by jamesrichardson on 10/30/15.
 */
public class AuthValidatorTest {

    public static class AsNonApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            // noop
        }
    }

    @BeforeClass
    public static void initJFX() {
        Flags.SetFlag(Flags.TESTMODE);
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(AsNonApp.class, new String[0]);

            }
        };
        t.setDaemon(true);
        t.start();
    }

    @Test
    public void testGetCooldown_repetition(){
        Flags.UnSetFlag(Flags.TESTMODE);

        AuthRequest req1 = new AuthRequest(AuthValidator.generateAuthToken(Settings.getDebugPassword()),(f)->{});
        AuthRequest req2 = new AuthRequest(AuthValidator.generateAuthToken(Settings.getDebugPassword()),(f)->{});
        AuthRequest req3 = new AuthRequest(AuthValidator.generateAuthToken(Settings.getDebugPassword()),(f)->{});
        AuthRequest req4 = new AuthRequest(AuthValidator.generateAuthToken(Settings.getDebugPassword()),(f)->{});

        //test human speed (slow)
        AuthValidator.performRequest(req1);
        Assert.assertEquals(false, AuthValidator.getCooldown());
        try {Thread.sleep(6000);}catch(Exception e){}
        AuthValidator.performRequest(req2);
        Assert.assertEquals(false,AuthValidator.getCooldown());

        //test computer speed (fast)
        AuthValidator.performRequest(req3);
        AuthValidator.performRequest(req4);
        Assert.assertEquals(true,AuthValidator.getCooldown());

        Flags.SetFlag(Flags.TESTMODE);
    }

    @Test
    public void testGetCooldown_failedLogin(){
        Flags.UnSetFlag(Flags.TESTMODE);

        int t = Integer.valueOf(Settings.GetSettingForKey(Setting.FAIL_COOLDOWN))+1;

        try {Thread.sleep(t*1000);}catch(Exception e){}

        AuthRequest req = new AuthRequest(Token.GenerateToken(),(f)->{});

        AuthValidator.performRequest(req);
        Assert.assertEquals(false, AuthValidator.getCooldown());
        try {Thread.sleep(6000);}catch(Exception e){}
        AuthValidator.performRequest(req);
        Assert.assertEquals(false, AuthValidator.getCooldown());
        try {Thread.sleep(6000);}catch(Exception e){}
        AuthValidator.performRequest(req);
        Assert.assertEquals(false, AuthValidator.getCooldown());
        try {Thread.sleep(6000);}catch(Exception e){}
        AuthValidator.performRequest(req);
        Assert.assertEquals(true, AuthValidator.getCooldown());

        Flags.SetFlag(Flags.TESTMODE);
    }

}

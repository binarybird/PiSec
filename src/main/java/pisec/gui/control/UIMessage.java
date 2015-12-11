package pisec.gui.control;

import pisec.gui.concurrency.TimerManager;

import java.util.Timer;
import java.util.function.Consumer;

/**
 * Created by binar on 10/18/2015.
 */
public class UIMessage {

    private final String message;
    private Timer timer = null;

    public UIMessage(String message){
        this.message = message;
    }

    public String getMessage(){return message;}

    public Timer startTimer(Consumer<Void> actionOnEnd){
        if(timer == null)
         this.timer = TimerManager.GetSharedTimerManager().createCountDown(7,intin->{if(intin <=0)actionOnEnd.accept(null);});

        return timer;
    }

    public void cancelTimer(){
        TimerManager.GetSharedTimerManager().CancleTimer(this.timer);
    }
}

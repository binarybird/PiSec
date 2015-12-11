package pisec.gui.concurrency;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Created by jamesrichardson on 10/16/15.
 */
public class TimerManager {

    private static TimerManager sharedTimerManager;
    private final  ArrayList<Timer> timerList = new ArrayList<Timer>();

    public static TimerManager GetSharedTimerManager(){
        if(sharedTimerManager == null){
            sharedTimerManager = new TimerManager();
        }
        return sharedTimerManager;
    }

    private TimerManager(){}

    public Timer createCountDown(final int countDownFromTime,final Consumer<Integer> actionOnPeriod){

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int countdown = countDownFromTime;
            @Override
            public void run() {
                actionOnPeriod.accept(countdown);
                countdown--;
                if(countdown < 0){
                    timer.cancel();
                    timer.purge();
                    timerList.remove(timer);
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);

        timerList.add(timer);

        return timer;
    }

    public Timer createCountUp(final int countUpToTime,final Consumer<Integer> actionOnPeriod){

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int countUp = countUpToTime;
            @Override
            public void run() {
                actionOnPeriod.accept(countUp);
                countUp++;
                if(countUp > 0){
                    timer.cancel();
                    timer.purge();
                    timerList.remove(timer);
                }
            }
        };
        timer.schedule(timerTask, 0, 1000);

        timerList.add(timer);

        return timer;
    }

    public Timer createSchedualedTask(final int period, final Consumer<Void> actionOnPeriod){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                actionOnPeriod.accept(null);
            }
        };
        timer.schedule(timerTask, 0, period);

        timerList.add(timer);

        return timer;
    }

    public void CancleAllTimers(){
        timerList.forEach(timer->{timer.cancel();timer.purge();});
        timerList.clear();
    }

    public void CancleTimer(Timer timer){
        timerList.forEach(e->{if(e==timer){timer.cancel();timer.purge();}});
        timerList.remove(timer);
    }

}

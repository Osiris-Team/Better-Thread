import com.osiris.betterthread.BetterThread;
import com.osiris.betterthread.BetterThreadDisplayer;
import com.osiris.betterthread.BetterThreadManager;
import com.osiris.betterthread.exceptions.JLineLinkException;

import java.util.function.Consumer;

public class UsageExample2 {

    public static void main(String[] args) throws JLineLinkException {
        BetterThreadManager manager = new BetterThreadManager();
        Consumer<BetterThread> run = thread -> {
            try {
                thread.addInfo("This is a sample info text."); // Gets printed once all tasks have finished in the summary.
                thread.addWarning("This is a sample warning!"); // Gets printed once all tasks have finished in the summary.
                while (!thread.isFinished()){
                    Thread.sleep(10);
                    thread.setStatus("Executing task... Step: "+thread.getNow());
                    System.out.println("TEST"); // This message gets printed once all tasks have finished
                    thread.step();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        BetterThreadDisplayer displayer = new BetterThreadDisplayer(manager,null, null, null, true);
        displayer.start();

        BetterThread t1 = new BetterThread(manager);
        t1.runAtStart = run;
        t1.start();
        BetterThread t2 = new BetterThread(manager);
        t2.runAtStart = run;
        t2.start();
        BetterThread t3 = new BetterThread(manager);
        t3.runAtStart = run;
        t3.start();
    }

}

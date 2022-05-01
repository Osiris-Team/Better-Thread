import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadPrinter;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.exceptions.JLineLinkException;

import java.util.function.Consumer;

public class UsageExample2 {

    public static void main(String[] args) throws JLineLinkException {
        BThreadManager manager = new BThreadManager();
        Consumer<BThread> run = thread -> {
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
        BThreadPrinter displayer = new BThreadPrinter(manager,null);
        displayer.start();

        BThread t1 = new BThread(manager);
        t1.runAtStart = run;
        t1.start();
        BThread t2 = new BThread(manager);
        t2.runAtStart = run;
        t2.start();
        BThread t3 = new BThread(manager);
        t3.runAtStart = run;
        t3.start();
    }

}

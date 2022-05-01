import com.osiris.betterthread.BThread;
import com.osiris.betterthread.BThreadPrinter;
import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.exceptions.JLineLinkException;

public class UsageExample {

    public static void main(String[] args) throws JLineLinkException {

        // 1. Create the manager
        BThreadManager manager = new BThreadManager();

        // 2. Create the displayer and pass over the manager
        BThreadPrinter displayer = new BThreadPrinter(manager);

        // 3. Create your tasks by extending BetterThread and overriding the runAtStart() method
        // Note: This is only for explanation purposes. In production create this class inside a package.
        class MyFirstTask extends BThread {

            public MyFirstTask(BThreadManager manager, boolean autoStart) {
                super(manager, autoStart);
            }

            @Override
            public void runAtStart() throws Exception {
                super.runAtStart();
                // 4. Insert your code to run here
                for (int i = 0; i < 100; i++) {
                    step();

                    setStatus("Task is running. Progress: "+getPercent()+"%");
                    Thread.sleep(400);
                }

            }
        }

        // 4. Run the tasks
        MyFirstTask task1 = new MyFirstTask(manager, true); // true to enable auto-start, so theres no need to call .start()
        MyFirstTask task2 = new MyFirstTask(manager, true);
        MyFirstTask task3 = new MyFirstTask(manager, true);
        MyFirstTask task4 = new MyFirstTask(manager, true);
        MyFirstTask task5 = new MyFirstTask(manager, true);
        MyFirstTask task6 = new MyFirstTask(manager, true);
        MyFirstTask task7 = new MyFirstTask(manager, true);
    }

}

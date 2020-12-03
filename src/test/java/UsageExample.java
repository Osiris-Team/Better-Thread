import com.osiris.betterthread.BetterThread;
import com.osiris.betterthread.BetterThreadDisplayer;
import com.osiris.betterthread.BetterThreadManager;

public class UsageExample {

    public static void main(String[] args) {

        // 1. Create the manager
        BetterThreadManager manager = new BetterThreadManager();

        // 2. Create the displayer and pass over the manager
        BetterThreadDisplayer displayer = new BetterThreadDisplayer(manager);

        // 3. Create your tasks by extending BetterThread and overriding the runAtStart() method
        // Note: This is only for explanation purposes. In production create this class inside a package.
        class MyFirstTask extends BetterThread{

            public MyFirstTask(BetterThreadManager manager) {
                super(manager);
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
        MyFirstTask task1 = new MyFirstTask(manager);
        MyFirstTask task2 = new MyFirstTask(manager);
        MyFirstTask task3 = new MyFirstTask(manager);
        MyFirstTask task4 = new MyFirstTask(manager);
        MyFirstTask task5 = new MyFirstTask(manager);
        MyFirstTask task6 = new MyFirstTask(manager);
        MyFirstTask task7 = new MyFirstTask(manager);

    }

}

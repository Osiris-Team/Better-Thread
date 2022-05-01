import com.osiris.betterthread.BThreadManager;
import com.osiris.betterthread.BThreadPrinter;
import com.osiris.betterthread.exceptions.JLineLinkException;
import com.osiris.betterthread.modules.BuilderBThreadModules;
import org.junit.jupiter.api.Test;

public class QuickStartExample {
    @Test
    void test() throws JLineLinkException {
        BThreadManager manager = new BThreadManager();
        manager.startPrinter();
        manager.start(thread -> {
            for (int i = 1; i <= 100; i++) {
                thread.setStatus("Climbing stairs... Step: "+ i);
                thread.step();
            }
        }, new BuilderBThreadModules().date().spinner().status().build());
    }
}

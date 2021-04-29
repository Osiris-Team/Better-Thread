package com.osiris.betterthread;

import com.osiris.betterthread.exceptions.JLineLinkException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BetterThreadDisplayerTest {

    @Test
    void test() throws JLineLinkException {
        BetterThreadManager manager = new BetterThreadManager();
        BetterThread test1 = new BetterThread(manager);
        test1.start();
        BetterThread test2 = new BetterThread(manager);
        test2.start();
        BetterThread test3 = new BetterThread(manager);
        test3.start();
        BetterThreadDisplayer displayer = new BetterThreadDisplayer(manager);
        displayer.start();

        // Its normal to get values over 100% because of this loop
        Thread thread = new Thread(()->{
            try {
                while (!manager.isFinished())
                    for (BetterThread t :
                            manager.getAll()) {
                        t.step();
                        Thread.sleep(10);
                    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        try{
            while (!manager.isFinished())
                Thread.sleep(1000);
            boolean isWarning = false;
            for (BetterThread t :
                    manager.getAll()) {
                if (!t.getBetterWarnings().isEmpty())
                    isWarning = true;
            }
            assertTrue(!isWarning);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
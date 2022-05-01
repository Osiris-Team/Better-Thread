package com.osiris.betterthread;

import com.osiris.betterthread.exceptions.JLineLinkException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BThreadDisplayerTest {

    @Test
    void test() throws JLineLinkException {
        BThreadManager manager = new BThreadManager();
        BThread test1 = new BThread(manager);
        test1.start();
        BThread test2 = new BThread(manager);
        test2.start();
        BThread test3 = new BThread(manager);
        test3.start();
        BThreadPrinter displayer = new BThreadPrinter(manager);
        displayer.start();

        // Its normal to get values over 100% because of this loop
        Thread thread = new Thread(()->{
            try {
                while (!manager.isFinished())
                    for (BThread t :
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
            for (BThread t :
                    manager.getAll()) {
                if (!t.getWarnList().isEmpty())
                    isWarning = true;
            }
            assertTrue(!isWarning);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
package com.osiris.betterthread;

import com.osiris.betterthread.jline.MyLine;
import org.jline.utils.AttributedString;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import static com.osiris.betterthread.Constants.*;

/**
 * Used for testing stuff in native
 * console windows.
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        for (int i = 0; i < 30; i++) {
            MY_DISPLAY.add("TEST");
        }


        //new Main().testNewThreadsGettingAddedWithTimeDelayAndInterveningMessages();

        //new Main().replaceMultipleLinesTest();
        //new Main().betterThreadDisplayerTest();
        MY_DISPLAY.add("SIOJASDIOASD");
        MY_DISPLAY.add("SIOJASDIOASD");
        MY_DISPLAY.add("SIOJASDIOASD");
        MY_DISPLAY.add("latest");
        Thread.sleep(1000);
        MY_DISPLAY.updateLines(new MyLine("BIG BOIII SKRR", MY_DISPLAY.getNewestLinesPosition()));
        Thread.sleep(1000);
        MY_DISPLAY.updateLines(new MyLine("GANG GANG WE LIT BOIIIII", MY_DISPLAY.getNewestLinesPosition()));
        Thread.sleep(1000);

        MY_DISPLAY.updateLines(new MyLine("GANG GANG WE LIT BOIIIII", MY_DISPLAY.getNewestLinesPosition()-3));
        Thread.sleep(1000);
        //new Main().betterThreadDisplayerTest();
    }


    private int progress = 0;

    void replaceMultipleLinesTest() throws InterruptedException, IOException {


        for (int i = 0; i < 30; i++) {
            TERMINAL.writer().println("TEST");
        }

        //resize();
        for (int i = 0; i < 5; i++) {
            DISPLAY.update(Arrays.asList(
                    AttributedString.fromAnsi("Line 1 withgbuioasdigufsUIGIUGgifuiguFUIGPfuigbpFUIGfuigf data "+new Random().nextLong()*new Random().nextLong()+ i),
                    AttributedString.fromAnsi("Line 2 withDFBUIBUPdfbupiFUIPfubpuFBUPFUPGBIFagbupUPGFDUGPfugpFAGUPUGFagufgufaGUOPAfoaui data "+new Random().nextLong()*new Random().nextLong() + i),
                    AttributedString.fromAnsi("Line 3 withSbupiasfBUPIFSbupBUBFbuUOBAf data "+new Random().nextLong()*new Random().nextLong() + i),
                    AttributedString.fromAnsi("Line 4 withaZ()?AFzfzfAZ()()ASF9ha89023u´´902387370c370232356 0256 0c325602560025025 79 0´257´90 0307´237´90235´7902´069 215´90 data "+new Random().nextLong()*new Random().nextLong() + i)
            ), -1); //-1
            Thread.sleep(1000);
        }
        TERMINAL.writer().println(" ");
    }

    void testNewThreadsGettingAddedWithTimeDelayAndInterveningMessages() throws InterruptedException {
        BetterThreadManager manager = new BetterThreadManager();

        BetterThreadDisplayer displayer = new BetterThreadDisplayer(manager);
        //displayer.setRefreshInterval(10);
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

        BetterThread t1 = new BetterThread(manager);
        t1.start();
        TERMINAL.writer().println("ABUSUIODAS");
        TERMINAL.writer().println("ABUSUIODAassdaS");
        Thread.sleep(2000);
        BetterThread t2 = new BetterThread(manager);
        t2.start();
        TERMINAL.writer().println("a");
        TERMINAL.writer().println("b");

        Thread.sleep(2000);
        BetterThread t3 = new BetterThread(manager);
        t3.start();

        try{
            while (!manager.isFinished())
                Thread.sleep(1000);
            boolean isWarning = false;
            for (BetterThread t :
                    manager.getAll()) {
                if (!t.getBetterWarnings().isEmpty())
                    isWarning = true;
            }
            //assertTrue(!isWarning);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void betterThreadDisplayerTest() {
        BetterThreadManager manager = new BetterThreadManager();
        BetterThread t1 = new BetterThread(manager);
        t1.start();
        BetterThread t2 = new BetterThread(manager);
        t2.start();
        BetterThread t3 = new BetterThread(manager);
        t3.start();
        BetterThreadDisplayer displayer = new BetterThreadDisplayer(manager);
        //displayer.setRefreshInterval(10);
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
            //assertTrue(!isWarning);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

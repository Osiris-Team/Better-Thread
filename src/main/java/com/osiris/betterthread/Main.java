package com.osiris.betterthread;

import com.osiris.betterthread.exceptions.JLineLinkException;
import com.osiris.betterthread.jline.MyPrintStream;
import org.jline.terminal.Size;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import static com.osiris.betterthread.Constants.*;

/**
 * Used for testing stuff in native
 * console windows.
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, JLineLinkException {
        new Main().betterThreadDisplayerTest();
        System.out.println("TEST");
        //MySection line = new MySection("Your lines content here!", 0);
        //MY_DISPLAY.updateLine(line);
        /*
        PrintStream old = System.out;
        MyPrintStream myPrintStream = new MyPrintStream();
        System.setOut(myPrintStream);
        System.out.println("THIS SHOULD NOT GET PRINTED");
        Display display = new Display(TERMINAL, false);
        Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
        display.resize(size.getRows(), size.getColumns());
        display.update(Arrays.asList(AttributedString.fromAnsi("THIS SHOULD BE SHOWN!")), -1);


        new Thread(()->{
            try{
                while (true){
                    Thread.sleep(1000);
                    Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
                    System.out.println("ROWS/HEIGHT: "+size.getRows()+" COLUMNS/WIDTH: "+ size.getColumns());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        for (int i = 0; i < 100; i++) {
            //TERMINAL.writer().println("TEST");
            MY_DISPLAY.add("TEST");
        }
// usual print
        Thread.sleep(1000);
        MY_DISPLAY.updateLine(0, "0");
        Thread.sleep(1000);
        MY_DISPLAY.updateLine(10, "10");
        Thread.sleep(1000);
        MY_DISPLAY.updateLine(25, "25");
        //new Main().betterThreadDisplayerTest();


        for (long i = 0; i < 10000; i++) {
            DISPLAY.update(Arrays.asList(AttributedString.fromAnsi("["+i+"] "+getRandomString())), -1);
        }



        MY_DISPLAY.updateLines(new MyLine("1 CHANGED "));
        MY_DISPLAY.updateLines(new MyLine("2 CHANGED "));
        MY_DISPLAY.updateLines(new MyLine("3 CHANGED "));
        Thread.sleep(1000);
        MY_DISPLAY.updateLines(new MyLine("1 CHANGED 21"+1+"", 0));
        MY_DISPLAY.updateLines(new MyLine("2 CHANGED 12"+33+"\n", 1));
        MY_DISPLAY.updateLines(new MyLine("3 CHANGED 12"+44+"\n", 2));
        Thread.sleep(1000);
        MY_DISPLAY.updateLines(new MyLine("1 CHANGED ", 0));
        MY_DISPLAY.updateLines(new MyLine("2 CHANGED ", 1));
        MY_DISPLAY.updateLines(new MyLine("3 CHANGED ", 2));
        */

        //new Main().testUpdatingLimit();
        /*
        for (int i = 0; i < 30; i++) {
            TERMINAL.writer().println("TEST");
        }

        // erase the lines
        Thread.sleep(1000);
        MY_DISPLAY.update(Collections.emptyList(), 0);
        // ... print lines to the terminal
        TERMINAL.writer().println("TEST");


        //new Main().testNewThreadsGettingAddedWithTimeDelayAndInterveningMessages();
*/
        //new Main().replaceMultipleLinesTest();
        //
        /*
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

         */
    }

    void testUpdatingLimit(){
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            //MY_DISPLAY.add("["+i+"] "+getRandomString());
        }
    }

    private static String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 200;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }


    private int progress = 0;


    void testNewThreadsGettingAddedWithTimeDelayAndInterveningMessages() throws InterruptedException, JLineLinkException {
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

    void betterThreadDisplayerTest() throws JLineLinkException {
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
                        System.out.println("TEST");
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

package com.osiris.betterthread;

import com.osiris.betterthread.exceptions.JLineLinkException;
import com.osiris.betterthread.modules.BThreadModulesBuilder;
import org.jline.terminal.Size;
import org.jline.utils.Display;

import java.io.IOException;
import java.util.Random;
import java.util.function.Consumer;

import static com.osiris.betterthread.Constants.TERMINAL;

/**
 * Used for testing stuff in native
 * console windows.
 */
public class Main {

    private final int progress = 0;

    public static void main(String[] args) throws IOException, InterruptedException, JLineLinkException {
        //new Main().testNewThreadsGettingAddedWithTimeDelayAndInterveningMessages();
        new Main().betterThreadDisplayerTest();



        /*
        Size newSize = TERMINAL.getSize();
        newSize.setRows(10);
        newSize.setColumns(10);
        TERMINAL.setSize(newSize);

        new Main().testUpdatingMoreLinesThanTerminalHasRows();
        Display dis1 = new Main().initAndGetNewDisplay();
        Display dis2 = new Main().initAndGetNewDisplay();



        Thread.sleep(1000);
        dis1.update(Collections.singletonList(AttributedString.fromAnsi("Updated display1")), TERMINAL.getSize().getColumns());
        Thread.sleep(1000);
        dis2.update(Collections.singletonList(AttributedString.fromAnsi("Updated display2")), -1);
        Thread.sleep(4000);

        //
        //new Main().betterThreadDisplayerTest();

        //MySection line = new MySection("Your lines content here!", 0);
        //MY_DISPLAY.updateLine(line);

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

    // Length 200
    private static String getRandomString() {
        return getRandomString(200);
    }

    private static String getRandomString(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    private Display initAndGetNewDisplay() {
        try {
            System.out.println("CREATED NEW DISPLAY");
            Display display = new Display(TERMINAL, false);
            Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
            display.resize(size.getRows(), size.getColumns());
            return display;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Display!");
        }
    }

    void testUpdatingLimit() {
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            //MY_DISPLAY.add("["+i+"] "+getRandomString());
        }
    }

    void testUpdatingMoreLinesThanTerminalHasRows() throws JLineLinkException {
        BThreadManager manager = new BThreadManager();
        BThreadPrinter displayer = new BThreadPrinter(manager);
        displayer.start();

        for (int i = 0; i < 20; i++) {
            BThread t = new BThread(manager);
            t.start();
        }

        Thread thread = new Thread(() -> {
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
    }

    void testNewThreadsGettingAddedWithTimeDelayAndInterveningMessages() throws InterruptedException, JLineLinkException {
        BThreadManager manager = new BThreadManager();

        BThreadPrinter displayer = new BThreadPrinter(manager);
        //displayer.setRefreshInterval(10);
        displayer.start();

        // Its normal to get values over 100% because of this loop
        Thread thread = new Thread(() -> {
            while (!manager.isFinished())
                for (BThread t :
                        manager.getAll()) {
                    t.step();
                }
        });
        thread.start();

        BThread t1 = new BThread(manager);
        t1.start();
        TERMINAL.writer().println("ABUSUIODAS");
        TERMINAL.writer().println("ABUSUIODAassdaS");
        Thread.sleep(2000);
        BThread t2 = new BThread(manager);
        t2.start();
        TERMINAL.writer().println("a");
        TERMINAL.writer().println("b");

        Thread.sleep(2000);
        BThread t3 = new BThread(manager);
        t3.start();

        try {
            while (!manager.isFinished())
                Thread.sleep(1000);
            boolean isWarning = false;
            for (BThread t :
                    manager.getAll()) {
                if (!t.getWarnList().isEmpty())
                    isWarning = true;
            }
            //assertTrue(!isWarning);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void betterThreadDisplayerTest() throws JLineLinkException {
        BThreadManager manager = new BThreadManager();
        Consumer<BThread> run = thread -> {
            try {
                thread.addInfo("This is a sample info text.");
                thread.addWarning("This is a sample warning!");
                while (!thread.isFinished()) {
                    Thread.sleep(100);
                    thread.setStatus(getRandomString(50));
                    System.out.println("TEST");
                    thread.step();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        BThreadPrinter displayer = new BThreadPrinter(manager, null);
        //displayer.setRefreshInterval(10);
        displayer.start();

        BThread t1 = new BThread(manager);
        t1.printerModules = new BThreadModulesBuilder().build();
        t1.runAtStart = run;
        t1.start();
        BThread t2 = new BThread(manager);
        t2.runAtStart = run;
        t2.start();
        BThread t3 = new BThread(manager);
        t3.runAtStart = run;
        t3.start();


        try {
            while (!manager.isFinished())
                Thread.sleep(1000);
            boolean isWarning = false;
            for (BThread t :
                    manager.getAll()) {
                if (!t.getWarnList().isEmpty())
                    isWarning = true;
            }
            //assertTrue(!isWarning);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

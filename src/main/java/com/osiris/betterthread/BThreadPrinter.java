/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;


import com.osiris.betterthread.exceptions.JLineLinkException;
import com.osiris.betterthread.jline.CachedPrintStream;
import com.osiris.betterthread.modules.BThreadPrinterModule;
import com.osiris.betterthread.modules.BThreadModulesBuilder;
import org.jline.terminal.Size;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.osiris.betterthread.Constants.TERMINAL;

/**
 * Uses jansi to display the live status of
 * all active BetterThreads.
 * Runs until there are no more active BetterThreads.
 */
public class BThreadPrinter extends Thread {
    private final Display display;
    /**
     * If the thread to be printed has no {@link BThread#printerModules} then,
     * these get used as fallback.
     */
    public List<BThreadPrinterModule> defaultPrinterModules = new BThreadModulesBuilder()
            .date().spinner().status().build();
    public boolean clearLinesOnFinish = false;
    private BThreadManager manager;
    private final LocalDateTime now = LocalDateTime.now();
    private boolean showWarnings;
    private boolean showDetailedWarnings;
    private int refreshInterval;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private byte anim;

    /**
     * Creates a new ThreadDisplayer with default
     * values set.
     * To customize these values use the other constructor.
     *
     * @param manager
     */
    public BThreadPrinter(BThreadManager manager) throws JLineLinkException {
        this(manager, null);
    }

    public BThreadPrinter(BThreadManager manager, DateTimeFormatter formatter) throws JLineLinkException {
        this(manager, formatter, false);
    }

    public BThreadPrinter(BThreadManager manager, DateTimeFormatter formatter, boolean showWarnings) throws JLineLinkException {
        this(manager, formatter, showWarnings, false);
    }

    public BThreadPrinter(BThreadManager manager, DateTimeFormatter formatter, boolean showWarnings, boolean showDetailedWarnings) throws JLineLinkException {
        this(manager, formatter, showWarnings, showDetailedWarnings, 250);
    }

    /**
     * @throws RuntimeException if there was an error getting the systems terminal.
     */
    public BThreadPrinter(BThreadManager manager, DateTimeFormatter formatter,
                          boolean showWarnings, boolean showDetailedWarnings, int refreshInterval) throws JLineLinkException {
        this.manager = manager;
        if (formatter != null) this.dateFormatter = formatter;
        this.showWarnings = showWarnings;
        this.showDetailedWarnings = showDetailedWarnings;
        this.refreshInterval = refreshInterval;
        try {
            // Init the display/section
            display = new Display(TERMINAL, false);
            Size size = TERMINAL.getSize(); // Need to initialize the size on the display with
            display.resize(size.getRows(), size.getColumns());
        } catch (Exception e) {
            JLineLinkException ex = new JLineLinkException("Failed to initialize JLines Display class! Details: " + e.getMessage());
            ex.setStackTrace(e.getStackTrace());
            throw ex;
        }

    }

    @Override
    public void run() {
        super.run();
        try {
            // Since we don't want any other messages
            // to get printed to the console, while we are doing or stuff,
            // we temporarily set the System.out to a custom PrintStream,
            // which captures all messages send during this period
            // and prints them when we are done.
            PrintStream originalOut = System.out;
            CachedPrintStream customOut = new CachedPrintStream();
            System.setOut(customOut);

            while (printAll()) {
                sleep(refreshInterval);
            }
            if(clearLinesOnFinish) display.update(new ArrayList<>(), -1); // Clear lines

            // Restore the output
            System.setOut(originalOut);

            // Print missed messages
            String c1 = customOut.getCache1();
            String c2 = customOut.getCache2().toString();
            if (!c1.trim().isEmpty()) System.out.println(c1);
            if (!c2.trim().isEmpty()) System.out.println(c2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the current status of all processes.
     * This method should be used with a pause of 250ms.
     * Returns false when all processes have finished!
     *
     * @return false when all processes have finished!
     */
    public boolean printAll() {
        List<AttributedString> linesToPrint = new ArrayList<>(); // Fill this list with threads details and update the console after this
        manager.getAll().forEach(thread -> { // Build a line for each thread.
            StringBuilder builder = new StringBuilder();
            if (thread.printerModules == null || thread.printerModules.isEmpty())
                thread.printerModules = defaultPrinterModules;
            for (BThreadPrinterModule m : thread.printerModules) {
                m.append(manager, this, thread, builder);
            }
            linesToPrint.add(AttributedString.fromAnsi(builder.toString()));
        });

        if (manager.getAll().isEmpty()) {
            linesToPrint.add(AttributedString.fromAnsi("No threads! Waiting..."));
        } else {
            if (manager.isFinished()) {
                display.update(linesToPrint, -1); // Update one last time
                if(!linesToPrint.get(linesToPrint.size()-1).contains('\n')) TERMINAL.writer().println();
                return false;
            }
        }

        display.update(linesToPrint, -1);
        return true;
    }

    private long getPercentage(long now, long max) {
        return (now * 100 / max);
    }

    /**
     * This is the link to the JLine API. <br>
     * {@link org.jline.utils.Display} is a section/part/paragraph of the terminal/console made out of lines. <br>
     * Those lines can get updated/added/removed by the {@link org.jline.utils.Display} class. <br>
     * In our case each line represents a {@link BThread}s progress <br>
     * and all those lines are updated/managed by this {@link org.jline.utils.Display}.
     */
    public Display getDisplay() {
        return display;
    }

    public BThreadManager getManager() {
        return manager;
    }

    public BThreadPrinter setManager(BThreadManager manager) {
        this.manager = manager;
        return this;
    }

    public boolean isShowWarnings() {
        return showWarnings;
    }

    public BThreadPrinter setShowWarnings(boolean showWarnings) {
        this.showWarnings = showWarnings;
        return this;
    }

    public boolean isShowDetailedWarnings() {
        return showDetailedWarnings;
    }

    public BThreadPrinter setShowDetailedWarnings(boolean showDetailedWarnings) {
        this.showDetailedWarnings = showDetailedWarnings;
        return this;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public BThreadPrinter setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
        return this;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public BThreadPrinter setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
        return this;
    }

}

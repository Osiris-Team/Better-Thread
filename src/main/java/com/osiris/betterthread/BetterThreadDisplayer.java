/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;


import org.fusesource.jansi.Ansi;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.Display;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.osiris.betterthread.Constants.DISPLAY;
import static com.osiris.betterthread.Constants.TERMINAL;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Uses jansi to display the live status of
 * all active BetterThreads.
 * Runs until there are no more active BetterThreads.
 */
public class BetterThreadDisplayer extends Thread {
    private BetterThreadManager manager;
    private String label = "[MyAppName]";
    private String threadType = "[PROCESS]";
    private LocalDateTime now;
    private boolean showWarnings;
    private boolean showDetailedWarnings;
    private int refreshInterval;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private List<BetterWarning> allWarnings = new ArrayList<>();

    private byte anim;

    /**
     * Creates a new ThreadDisplayer with default
     * values set.
     * To customize these values use the other constructor.
     * @param manager
     */
    public BetterThreadDisplayer(BetterThreadManager manager) {
        this(manager, null);
    }

    public BetterThreadDisplayer(BetterThreadManager manager, String label) {
        this(manager, label, null);
    }

    public BetterThreadDisplayer(BetterThreadManager manager, String label, String threadType) {
        this(manager, label, threadType, null);
    }

    public BetterThreadDisplayer(BetterThreadManager manager, String label, String threadType,
                                 DateTimeFormatter formatter) {
        this(manager, label, threadType, formatter, false);
    }

    public BetterThreadDisplayer(BetterThreadManager manager, String label, String threadType,
                                 DateTimeFormatter formatter, boolean showWarnings) {
        this(manager, label, threadType, formatter, showWarnings, false);
    }

    public BetterThreadDisplayer(BetterThreadManager manager, String label, String threadType,
                                 DateTimeFormatter formatter, boolean showWarnings, boolean showDetailedWarnings) {
        this(manager, label, threadType, formatter, showWarnings, showDetailedWarnings, 250);
    }

    /**
     * Creates a new ThreadDisplayer.
     * You can customize very aspect of it by
     * passing over the wanted values.
     * @param manager
     * @param label
     * @param threadType
     * @param formatter
     * @param showWarnings
     * @param showDetailedWarnings
     * @param refreshInterval
     * @throws RuntimeException if there was an error getting the systems terminal.
     */
    public BetterThreadDisplayer(BetterThreadManager manager, String label, String threadType, DateTimeFormatter formatter,
                                 boolean showWarnings, boolean showDetailedWarnings, int refreshInterval) {
        this.manager = manager;
        if (label!=null) this.label = label;
        if (threadType!=null) this.threadType = threadType;
        if (formatter!=null) this.dateFormatter = formatter;
        this.showWarnings = showWarnings;
        this.showDetailedWarnings = showDetailedWarnings;
        this.refreshInterval = refreshInterval;

        // Check if Jansi console was already started
        //AnsiConsole.systemInstall();
    }

    @Override
    public void run() {
        super.run();
        try{
            //resize();
            while (printAll()){
                sleep(refreshInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the current status of all processes.
     * This method should be used with a pause of 250ms.
     * Returns false when all processes have finished!
     * @return false when all processes have finished!
     */
    public boolean printAll(){

        // Get current date
        now = LocalDateTime.now();

        // Fill this list with threads details and update the console after this
        List<AttributedString> list = new ArrayList<>();
        manager.getAll().forEach(thread -> {
            // Display the information for each thread.
            // Each thread gets one line.
            StringBuilder builder = new StringBuilder();

            //Format the output for a single process
            //First add the date, label and process info labels

            builder.append(ansi()
                    .bg(WHITE)
                    .fg(BLACK).a("["+dateFormatter.format(now)+"]")
                    .fg(CYAN).a(label)
                    .fg(BLACK).a(threadType)
                    .reset());

            //Add the loading animation

            if (thread.isFinished()){
                if(thread.isSkipped()){
                    builder.append(ansi()
                            .fg(BLUE).a(" [#] ")
                            .reset());
                }
                else if (thread.isSuccess()){
                    builder.append(ansi()
                            .fg(GREEN).a(" [#] ")
                            .reset());
                }
                else{
                    builder.append(ansi()
                            .fg(RED).a(" [#] ")
                            .reset());
                }
            }
            else{
                switch (anim) {
                    case 1:
                        builder.append(ansi().a(" [\\] "));
                        break;
                    case 2:
                        builder.append(ansi().a(" [|] "));
                        break;
                    case 3:
                        builder.append(ansi().a(" [/] "));
                        break;
                    default:
                        anim = 0;
                        builder.append(ansi().a(" [-] "));
                }
            }



            // Add the actual process details and finish the line
            final String name = thread.getName();
            final long now = thread.getNow();
            final long max = thread.getMax();
            final byte percent  = thread.getPercent();
            final String status = thread.getStatus();

            if (now > 0){
                if (thread.isSkipped())
                    builder.append(ansi()
                            .a("> ["+name+"] "+status));
                else
                    builder.append(ansi()
                            .a("> ["+name+"]["+percent+"%] "+status));
            }
            else{
                builder.append(ansi()
                        .a("> ["+name+"] "+status));
            }
            builder.append(ansi().reset());

            // Add this message to the list
            list.add(AttributedString.fromAnsi(builder.toString()));
        });

        // This must be done outside the for loop otherwise the animation wont work
        anim++;

        if (manager.getAll().size()==0){
            list.add(AttributedString.fromAnsi("No threads! Waiting..."));
        }
        else{
            // This means we finished and should stop looping
            // We print the last warnings message and stop.
            if (manager.isFinished()){
                DISPLAY.update(list, -1); // Update one last time
                this.allWarnings = manager.getAllWarnings();
                TERMINAL.writer().println(" ");
                formatWarnings();
                return false;
            }
        }

        DISPLAY.update(list, -1);
        return true;
    }

    private long getPercentage(long now, long max){
        return (now*100/max);
    }

    /**
     * This is will be shown when all processes finished.
     */
    private void formatWarnings(){

        Ansi ansiDate = ansi()
                .bg(WHITE)
                .fg(BLACK).a("["+dateFormatter.format(now)+"]")
                .fg(CYAN).a(label)
                .fg(BLACK).a("[SUMMARY]")
                .reset();

        if (allWarnings.isEmpty()){
            TERMINAL.writer().println(ansi()
                    .a(ansiDate)
                    .fg(GREEN)
                    .a(" Executed all tasks successfully!")
                    .reset());
        }
        else if (showWarnings) {
            TERMINAL.writer().println(ansi()
                    .a(ansiDate)
                    .fg(YELLOW)
                    .a(" There are " + allWarnings.size() + " warnings:")
                    .reset());

            if (showDetailedWarnings) {
                BetterWarning betterWarning;
                for (int i = 0; i < allWarnings.size(); i++) {
                    betterWarning = allWarnings.get(i);
                    StringBuilder builder = new StringBuilder();
                    builder.append(ansiDate);
                    builder.append(ansi()
                            .fg(YELLOW).a("[WARNING-" + i + "][" + betterWarning.getThread().getName() + "][Message: " + betterWarning.getException().getMessage() +
                                    "][Cause: " + betterWarning.getException().getCause() +
                                    "][Extra: " + betterWarning.getExtraInfo() +
                                    "][Trace: " + Arrays.toString(betterWarning.getException().getStackTrace())).reset());
                    TERMINAL.writer().println(builder.toString());
                }
            }
            else {
                BetterWarning betterWarning;
                for (int i = 0; i < allWarnings.size(); i++) {
                    betterWarning = allWarnings.get(i);
                    StringBuilder builder = new StringBuilder();
                    builder.append(ansiDate);
                    builder.append(ansi()
                            .fg(YELLOW).a("[WARNING-" + i + "][" + betterWarning.getThread().getName() + "][Message: " + betterWarning.getException().getMessage() + "]").reset());
                    TERMINAL.writer().println(builder.toString());
                }
            }
        }
        else{
            TERMINAL.writer().println(ansi()
                    .fg(YELLOW).a(" There are "+ allWarnings.size()+" warnings! Enable 'show-warnings' to view them, or check your log for further details!")
                    .reset());
        }
    }

    public BetterThreadManager getManager() {
        return manager;
    }

    public void setManager(BetterThreadManager manager) {
        this.manager = manager;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getThreadType() {
        return threadType;
    }

    public void setThreadType(String threadType) {
        this.threadType = threadType;
    }

    public boolean isShowWarnings() {
        return showWarnings;
    }

    public void setShowWarnings(boolean showWarnings) {
        this.showWarnings = showWarnings;
    }

    public boolean isShowDetailedWarnings() {
        return showDetailedWarnings;
    }

    public void setShowDetailedWarnings(boolean showDetailedWarnings) {
        this.showDetailedWarnings = showDetailedWarnings;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    public void setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public List<BetterWarning> getAllWarnings() {
        return allWarnings;
    }

    public void setAllWarnings(List<BetterWarning> allWarnings) {
        this.allWarnings = allWarnings;
    }
}

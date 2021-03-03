/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;


import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.Display;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Uses jansi to display the live status of
 * all active BetterThreads.
 * Runs until there are no more active BetterThreads.
 */
public class BetterThreadDisplayer extends Thread {
    private Terminal terminal;
    private Display display;
    private BetterThreadManager manager;
    private String label = "[MyAppName]";
    private String threadType = "[PROCESS]";
    private LocalDateTime now;
    private boolean showWarnings;
    private boolean showDetailedWarnings;
    private int refreshInterval;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private List<BetterWarning> allWarnings = new ArrayList<>();

    private static byte anim;

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
        // AnsiConsole.systemInstall();
        try{
            terminal = TerminalBuilder.terminal();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("There was an error getting the systems terminal!");
        }
    }

    @Override
    public void run() {
        super.run();
        try{
            resize();
            while (printAll()){
                sleep(refreshInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resize(){
        display = new Display(terminal, false);
        Size size = terminal.getSize(); // Need to initialize the size on the display with
        display.resize(size.getRows(), size.getColumns());
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

        if (manager.getAll().size()==0){
            System.out.println(ansi().a("No threads! Waiting..."));
        }
        else{
            for (int i = 0; i < manager.getAll().size(); i++) {

                //Get one single process
                BetterThread process = manager.getAll().get(i);

                //Format the output for a single process
                //First add the date, label and process info labels
                System.out.print(ansi()
                        .bg(WHITE)
                        .fg(BLACK).a("["+dateFormatter.format(now)+"]")
                        .fg(CYAN).a(label)
                        .fg(BLACK).a(threadType)
                        .reset());

                //Add the loading animation
                if (process.isFinished()){
                    if (process.isSuccess()){
                        System.out.print(ansi()
                                .fg(GREEN).a(" [#] ")
                                .reset());
                    }
                    else if(process.isSkipped()){
                        System.out.print(ansi()
                                .fg(BLUE).a(" [#] ")
                                .reset());
                    }
                    else{
                        System.out.print(ansi()
                                .fg(RED).a(" [#] ")
                                .reset());
                    }
                }
                else{
                    switch (anim) {
                        case 1:
                            System.out.print(ansi().a(" [\\] "));
                            break;
                        case 2:
                            System.out.print(ansi().a(" [|] "));
                            break;
                        case 3:
                            System.out.print(ansi().a(" [/] "));
                            break;
                        default:
                            anim = 0;
                            System.out.print(ansi().a(" [-] "));
                    }
                }


                // Add the actual process details and finish the line
                final String name = process.getName();
                final long now = process.getNow();
                final long max = process.getMax();
                final byte percent = process.getPercent();
                final String status= process.getStatus();

                if (now >0){
                    if (process.isSkipped())
                        System.out.print(ansi()
                            .a("> ["+name+"] "+status));
                    else
                        System.out.print(ansi()
                            .a("> ["+name+"]["+percent+"%] "+status));
                }
                else{
                    System.out.print(ansi()
                            .a("> ["+name+"] "+status));
                }
                System.out.println(ansi().reset());
            }
            // This must be done outside the for loop otherwise the animation wont work
            anim++;

            // This means we finished and should stop looping
            // We print the last warnings message and stop.
            if (manager.isFinished()){
                this.allWarnings = manager.getAllWarnings();
                formatWarnings();
                return false;
            }

        }
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
            System.out.println(ansi()
                    .a(ansiDate)
                    .fg(GREEN)
                    .a(" Executed all tasks successfully!")
                    .reset());
        }
        else if (showWarnings) {
            System.out.println(ansi()
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
                    System.out.println(builder.toString());
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
                    System.out.println(builder.toString());
                }
            }
        }
        else{
            System.out.println(ansi()
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

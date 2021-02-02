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
    private BetterThreadManager manager;
    private String label = "[MyAppName]";
    private String threadType = "[PROCESS]";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private LocalDateTime now;
    private boolean showWarnings;
    private boolean showDetailedWarnings;
    private int refreshInterval;

    private static byte anim;

    /**
     * Creates a new ThreadDisplayer with default
     * values set.
     * To customize these values use the other constructor.
     * @param manager
     */
    public BetterThreadDisplayer(BetterThreadManager manager) {
        this(manager, null, null, null, true, false, 250);
        this.manager = manager;
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
        AnsiConsole.systemInstall();
        this.start();
    }

    @Override
    public void run() {
        super.run();
        try{
            savePos();
            while (printAll()){
                sleep(refreshInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current cursor position.
     * Call this method once before starting the printall loop, so the cursor jumps back to this position.
     */
    public void savePos(){
        // Save cursor position so we can go back and update the lines.
        System.out.print(ansi().saveCursorPosition());
    }

    private void restoreAndCleanPos(){
        System.out.print(ansi().restoreCursorPosition());
        System.out.print(ansi().eraseLine(Ansi.Erase.FORWARD));
        System.out.print(ansi().restoreCursorPosition());
    }

    /**
     * Prints the current status of all processes.
     * This method should be used with a pause of 250ms.
     * Returns false when all processes have finished!
     * @return false when all processes have finished!
     */
    public boolean printAll(){
        // Restore position
        restoreAndCleanPos();

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

                if (now>0){
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
            if (manager.getActive().isEmpty()){
                // Check if there are no threads that weren't started yet
                boolean isPendingStart = false;
                for (BetterThread t :
                        manager.getAll()) {
                    if (!t.isStarted()){
                        isPendingStart = true;
                        break;
                    }
                }
                if (!isPendingStart){
                    formatWarnings();
                    return false;
                }
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
        List<BetterWarning> allBetterWarnings = new ArrayList();

        // Go through every process and add their warnings to the allBetterWarnings list
        for (BetterThread process :
                manager.getAll()) {
            List<BetterWarning> betterWarnings = process.getWarnings();
            if (!betterWarnings.isEmpty()){
                allBetterWarnings.addAll(betterWarnings);
            }
        }


        Ansi ansiDate = ansi()
                .bg(WHITE)
                .fg(BLACK).a("["+dateFormatter.format(now)+"]")
                .fg(CYAN).a(label)
                .fg(BLACK).a("[SUMMARY]")
                .reset();

        if (allBetterWarnings.isEmpty()){
            System.out.print(ansi()
                    .fg(GREEN).a(" Executed all tasks successfully!")
                    .reset());
        }
        else if (showWarnings) {
            System.out.print(ansi()
                    .fg(YELLOW).a(" There are " + allBetterWarnings.size() + " warnings:")
                    .reset());

            if (showDetailedWarnings) {
                BetterWarning betterWarning;
                for (int i = 0; i < allBetterWarnings.size(); i++) {
                    betterWarning = allBetterWarnings.get(i);
                    StringBuilder builder = new StringBuilder();
                    builder.append(ansiDate);
                    builder.append(ansi()
                            .bg(WHITE)
                            .fg(BLACK).a("[" + dateFormatter.format(now) + "]")
                            .fg(CYAN).a(label)
                            .reset().fg(YELLOW).a("[WARNING-" + i + "][" + betterWarning.getThread().getName() + "][Message: " + betterWarning.getException().getMessage() +
                                    "][Cause: " + betterWarning.getException().getCause() +
                                    "][Extra: " + betterWarning.getExtraInfo() +
                                    "][Trace: " + Arrays.toString(betterWarning.getException().getStackTrace())).reset());
                    System.out.print(builder.toString());
                }
            }
            else {
                BetterWarning betterWarning;
                for (int i = 0; i < allBetterWarnings.size(); i++) {
                    betterWarning = allBetterWarnings.get(i);
                    StringBuilder builder = new StringBuilder();
                    builder.append(ansiDate);
                    builder.append(ansi()
                            .bg(WHITE)
                            .fg(BLACK).a("[" + dateFormatter.format(now) + "]")
                            .fg(CYAN).a(label)
                            .reset().fg(YELLOW).a("[WARNING-" + i + "][" + betterWarning.getThread().getName() + "][Message: " + betterWarning.getException().getMessage() + "]").reset());
                    System.out.print(builder.toString());
                }
            }
        }
        else{
            System.out.print(ansi()
                    .fg(YELLOW).a(" There are "+ allBetterWarnings.size()+" warnings! Enable 'show-warnings' in the to view them, or check your debug log for further details!")
                    .reset());
        }
    }

}

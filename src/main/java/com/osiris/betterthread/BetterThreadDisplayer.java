/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;

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
public class BetterThreadDisplayer {
    private BetterThreadManager manager;
    private String label = "[MyAppName]";
    private String threadType = "[PROCESS]";
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private LocalDateTime now;
    private boolean showWarnings;
    private boolean showDetailedWarnings;
    private int refreshInterval;

    private int lineCounter = 0;
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

        savePos();

        Thread thread = new Thread(()->{
            try{
                while (printAll()){
                    Thread.sleep(refreshInterval);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    /**
     * Saves the current cursor position.
     * Call this method once before starting the printall loop, so the cursor jumps back to this position.
     */
    public void savePos(){
        // Save cursor position so we can go back and update the lines.
        System.out.println(ansi().saveCursorPosition());
    }

    private void restoreAndCleanPos(){
        System.out.print(ansi().restoreCursorPosition());
        for (int i = 0; i < lineCounter; i++) {
            System.out.println(ansi().eraseLine());
        }
        System.out.print(ansi().restoreCursorPosition());
        lineCounter=0;
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
            System.out.println("No threads! Waiting...");
            lineCounter++;
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
                            System.out.print(" [\\] ");
                            break;
                        case 2:
                            System.out.print(" [|] ");
                            break;
                        case 3:
                            System.out.print(" [/] ");
                            break;
                        default:
                            anim = 0;
                            System.out.print(" [-] " );
                    }
                }


                // Add the actual process details and finish the line
                final String name =process.getName();
                final long now = process.getNow();
                final long max = process.getMax();
                final String status= process.getStatus();

                if (now>0){
                    if (process.isSkipped())
                        System.out.print(ansi()
                            .a("> ["+name+"] "+status)
                            .reset()+"\n");
                    else
                        System.out.print(ansi()
                            .a("> ["+name+"]["+getPercentage(now,max)+"%] "+status)
                            .reset()+"\n");
                }
                else{
                    System.out.print(ansi()
                            .a("> ["+name+"] "+status)
                            .reset()+"\n");
                }

                lineCounter++;
            }
            // This must be done outside the for loop otherwise the animation wont work
            anim++;

            // This means we finished and should stop looping
            // We print the last warnings message and stop.
            if (manager.getActive().isEmpty()){
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

        System.out.print(ansi()
                .bg(WHITE)
                .fg(BLACK).a("["+dateFormatter.format(now)+"]")
                .fg(CYAN).a(label)
                .fg(BLACK).a("[SUMMARY]")
                .reset());

        List<BetterWarning> allBetterWarnings = new ArrayList();

        // Go through every process and add their warnings to the allBetterWarnings list
        for (BetterThread process :
                manager.getAll()) {
            List<BetterWarning> betterWarnings = process.getWarnings();
            if (!betterWarnings.isEmpty()){
                allBetterWarnings.addAll(betterWarnings);
            }
        }

        if (allBetterWarnings.isEmpty()){
            System.out.print(ansi()
                    .fg(GREEN).a(" Executed all tasks successfully!")
                    .reset()+"\n");
        }
        else if (showWarnings) {
            System.out.print(ansi()
                    .fg(YELLOW).a(" There are " + allBetterWarnings.size() + " warnings:")
                    .reset() + "\n");

            if (showDetailedWarnings) {
                BetterWarning betterWarning;
                for (int i = 0; i < allBetterWarnings.size(); i++) {
                    betterWarning = allBetterWarnings.get(i);
                    System.out.println(ansi()
                            .bg(WHITE)
                            .fg(BLACK).a("[" + dateFormatter.format(now) + "]")
                            .fg(CYAN).a(label)
                            .reset().fg(YELLOW).a("[WARNING-" + i + "][" + betterWarning.getThread().getName() + "][Message: " + betterWarning.getException().getMessage() +
                                    "][Cause: " + betterWarning.getException().getCause() +
                                    "][Extra: " + betterWarning.getExtraInfo() +
                                    "][Trace: " + Arrays.toString(betterWarning.getException().getStackTrace())).reset());
                }
            }
            else {
                BetterWarning betterWarning;
                for (int i = 0; i < allBetterWarnings.size(); i++) {
                    betterWarning = allBetterWarnings.get(i);
                    System.out.println(ansi()
                            .bg(WHITE)
                            .fg(BLACK).a("[" + dateFormatter.format(now) + "]")
                            .fg(CYAN).a(label)
                            .reset().fg(YELLOW).a("[WARNING-" + i + "][" + betterWarning.getThread().getName() + "][Message: " + betterWarning.getException().getMessage() + "]").reset());
                }
            }
        }
        else{
            System.out.print(ansi()
                    .fg(YELLOW).a(" There are "+ allBetterWarnings.size()+" warnings! Enable 'show-warnings' in the to view them, or check your debug log for further details!")
                    .reset()+"\n");
        }
    }

}

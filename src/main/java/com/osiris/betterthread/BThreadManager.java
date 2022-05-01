/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;

import com.osiris.betterthread.exceptions.JLineLinkException;
import com.osiris.betterthread.modules.BThreadPrinterModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Contains information about active
 * and inactive threads.
 */
public class BThreadManager {
    private List<BThread> all = new CopyOnWriteArrayList<>();
    private List<BThread> active = new CopyOnWriteArrayList<>();
    private boolean finished;

    /**
     * Runs the provided code in a new thread, attached to this {@link BThreadManager}.
     */
    public BThreadManager start(Consumer<BThread> runnable) {
        BThread t = new BThread(this);
        t.runAtStart = runnable;
        t.start();
        return this;
    }

    /**
     * Runs the provided code in a new thread, attached to this {@link BThreadManager}.
     */
    public BThreadManager start(Consumer<BThread> runnable, List<BThreadPrinterModule> printerModules) {
        BThread t = new BThread(this);
        t.runAtStart = runnable;
        t.printerModules = printerModules;
        t.start();
        return this;
    }

    /**
     * A list containing all current and finished threads.
     */
    public List<BThread> getAll() {
        return all;
    }

    public void setAll(List<BThread> all) {
        this.all = all;
    }

    /**
     * A list containing all current active threads.
     * A thread removes itself from this list when it finishes running.
     */
    public List<BThread> getActive() {
        return active;
    }

    public void setActive(List<BThread> active) {
        this.active = active;
    }

    /**
     * Convenience method for removing
     * all threads attached to the lists.
     */
    public void clear() {
        all.clear();
        active.clear();
    }

    /**
     * Returns true when there is a not started Thread in the list.
     */
    public boolean threadsPendingStart() {
        for (BThread t :
                all) {
            if (!t.isStarted()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all warnings from all threads, that were
     * added to this manager.
     */
    public List<BWarning> getAllWarnings() {
        List<BWarning> list = new ArrayList<>();
        for (BThread process :
                all) {
            List<BWarning> bWarnings = process.getWarnings();
            if (!bWarnings.isEmpty()) {
                list.addAll(bWarnings);
            }
        }
        return list;
    }

    /**
     * It only returns true, if there are no more active threads left and none with a pending start. <br>
     * IMPORTANT: <br>
     * If there weren't threads added to this manager yet, this method returns ALSO TRUE. <br>
     * This is done due to performance reasons. <br>
     */
    public boolean isFinished() {
        if (active.isEmpty() && !threadsPendingStart()) {
            return finished = true;
        }
        return finished = false;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Starts a printer thread that prints thread data to the terminal
     * until this manager has no more running threads.
     */
    public BThreadPrinter startPrinter() throws JLineLinkException {
        BThreadPrinter printer = new BThreadPrinter(this);
        printer.start();
        return printer;
    }
}

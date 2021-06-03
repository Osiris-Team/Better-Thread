/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Contains information about active
 * and inactive threads.
 */
public class BetterThreadManager {
    private List<BetterThread> all = new CopyOnWriteArrayList<>();
    private List<BetterThread> active = new CopyOnWriteArrayList<>();
    private boolean finished;

    /**
     * A list containing all current and finished threads.
     */
    public List<BetterThread> getAll() {
        return all;
    }

    public void setAll(List<BetterThread> all) {
        this.all = all;
    }

    /**
     * A list containing all current active threads.
     * A thread removes itself from this list when it finishes running.
     */
    public List<BetterThread> getActive() {
        return active;
    }

    public void setActive(List<BetterThread> active) {
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
        for (BetterThread t :
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
    public List<BetterWarning> getAllWarnings() {
        List<BetterWarning> list = new ArrayList<>();
        for (BetterThread process :
                all) {
            List<BetterWarning> betterWarnings = process.getWarnings();
            if (!betterWarnings.isEmpty()) {
                list.addAll(betterWarnings);
            }
        }
        return list;
    }

    public boolean isFinished() {
        // This means that all tasks have finished
        if (active.isEmpty()) {
            // Check if there are no threads that weren't started yet
            if (!threadsPendingStart()) {
                return finished = true;
            }
        }
        return finished = false;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}

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

/**
 * Contains information about active
 * and inactive threads.
 */
public class BetterThreadManager {
    private List<BetterThread> all = new ArrayList<>();;
    private List<BetterThread> active = new ArrayList<>();;

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
    public void clear(){
        all.clear();
        active.clear();
    }

}

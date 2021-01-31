/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;

public class BetterWarning {

    private BetterThread thread;
    private Exception e;
    private String extraInfo;

    /**
     * A warning is used to display irregularities to the user.
     * Warnings are only used in BetterThreads (asynchronously running tasks).
     * For example when a plugin update failed.
     * Warnings may not visible to the user directly.
     * @param thread pass over the thread, so we can get its name.
     * @param e the Exception.
     */
    public BetterWarning(BetterThread thread, Exception e) {
        this(thread, e, null);
    }

    public BetterWarning(BetterThread thread, Exception e, String extraInfo){
        this.thread = thread;
        this.e = e;
        this.extraInfo = extraInfo;
    }

    /**
     * Returns the thread this warning came from.
     * @return
     */
    public BetterThread getThread() {
        return thread;
    }

    public Exception getException() {
        return e;
    }

    public String getExtraInfo() {
        return extraInfo;
    }
}

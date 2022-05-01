/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.betterthread;

import java.util.List;

/**
 * This interface enables us to create threads
 * which return detailed status reports to their current progress.
 */
public interface DisplayableThread {

    /**
     * Get the minimum progress value. Usually starting at 0.
     *
     * @return minimum.
     */
    long getMin();

    /**
     * Get the maximum progress value. If the getNow() value equals this it means the process has finished.
     *
     * @return maximum.
     */
    long getMax();

    /**
     * Get the current progress value.
     *
     * @return current.
     */
    long getNow();

    /**
     * Get the current status of a thread.
     * Every thread chooses for itself what to display and with how much detail.
     *
     * @return status string.
     */
    String getStatus();

    /**
     * Is the thread running?
     *
     * @return true if thread finished.
     */
    boolean isFinished();

    /**
     * Was the operation successful?
     *
     * @return true is it was.
     */
    boolean isSuccess();

    /**
     * A list containing warnings.
     * Warnings are errors that occurred during execution.
     *
     * @return null if there were no warnings.
     */
    List<BWarning> getWarnings();

}

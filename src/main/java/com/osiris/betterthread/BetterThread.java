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
import java.util.function.Consumer;

/**
 * This class was build to be extended.
 * It contains the core functionality.
 */
public class BetterThread extends Thread implements DisplayableThread {
    private BetterThreadManager manager;
    private long min;
    private long max;
    private long now;
    private String status = "Initialising...";
    private boolean finished = false;
    private boolean skipped = false;
    private boolean success = true;
    private boolean autoStart = false;
    private boolean autoFinish = true;
    private boolean started = false;
    private List<BetterWarning> warnList = new ArrayList<>();
    private List<String> infoList = new ArrayList<>();
    public Consumer<BetterThread> runAtStart;

    /**
     * Creates a new thread and runs it.
     * The default values 0(min), 100(max) and 0(now) are used.
     * You can change them by using the set methods or a different constructor.
     * You need to pass over the threadManagers instance.
     *
     * @param manager the BetterThreadManager.
     */
    public BetterThread(BetterThreadManager manager) {
        this(null, 0, 100, 0, manager);
    }

    /**
     * Convenience method for directly starting the thread, because auto-start is actually false.
     * {@link #BetterThread(BetterThreadManager)}
     */
    public BetterThread(BetterThreadManager manager, boolean autoStart) {
        this(null, 0, 100, 0, manager, autoStart, true);
    }

    /**
     * {@link #BetterThread(BetterThreadManager)}
     */
    public BetterThread(BetterThreadManager manager, boolean autoStart, boolean autoFinish) {
        this(null, 0, 100, 0, manager, autoStart, autoFinish);
    }


    /**
     * Creates a new thread and runs it.
     * The default values 0(min), 100(max) and 0(now) are used.
     * You can change them by using the set methods or a different constructor.
     * You need to pass over the threadManagers instance.
     *
     * @param name    set this threads name.
     * @param manager the BetterThreadManager.
     */
    public BetterThread(String name, BetterThreadManager manager) {
        this(name, 0, 100, 0, manager);
    }

    /**
     * Creates a new thread. And starts the thread.
     * Min, max and now are int variables.
     *
     * @param min The minimum. Usually the starting point.
     * @param max The maximum. Usually when the thread finishes.
     * @param now The current progress of the thread.
     */
    public BetterThread(String name, int min, long max, long now, BetterThreadManager manager) {
        this(name, min, max, now, manager, false, true);
    }

    /**
     * Creates a new thread. And starts the thread.
     * Min, max and now are long variables.
     *
     * @param min        The minimum. Usually the starting point.
     * @param max        The maximum. Usually when the thread finishes.
     * @param now        The current progress of the thread.
     * @param manager    The parent ThreadManager this Thread should be added to.
     * @param autoStart  Starts the thread automatically. No need to call the start() method. Default it false.
     * @param autoFinish Finishes when the now value equals the max value.
     *                   Its checked every time step()/isFinished() is called.
     *                   Default is true. If you disable this you will have to call the finish() method by yourself.
     */
    public BetterThread(String name, long min, long max, long now, BetterThreadManager manager,
                        boolean autoStart, boolean autoFinish) {
        this.min = min;
        this.max = max;
        this.now = now;
        this.manager = manager;
        this.autoStart = autoStart;
        this.autoFinish = autoFinish;
        initEssentials(name);
    }

    private void initEssentials(String name) {
        configureName(name);
        addToProcesses();
        if (autoStart) start();
    }

    private void addToProcesses() {
        manager.getAll().add(this);
    }

    private void addToActiveProcesses() {
        manager.getActive().add(this);
    }

    /**
     * Sets the Threads name.
     * If name = null, we use the Threads count as name: "Thread+number".
     * This is done automatically already.
     *
     * @param name
     */
    private void configureName(String name) {
        if (name == null) {
            setName(this.getClass().getSimpleName());
        } else {
            setName(name);
        }
    }

    /**
     * Do NOT use this method to run a thread.
     * A {@link BetterThread} usually gets started automatically when you create one! See {@link #isAutoStart()} for details.
     * If you want to specify the code this thread should run,
     * use/override the runAtStart() method!
     */
    @Override
    public void run() {
        try {
            super.run();
            addToActiveProcesses();
            started = true;
            runAtStart();
            if (autoFinish)
                finish(true);
        } catch (Exception e) {
            setSuccess(false);
            if (e.getMessage() != null) setStatus(e.getMessage());
            getWarnings().add(new BetterWarning(this, e));
        } catch (Throwable t){
            setSuccess(false);
            if (t.getMessage() != null) setStatus(t.getMessage());
            getWarnings().add(new BetterWarning(this, t));
        }
    }

    /**
     * Override this method and enter the code you want this
     * thread to run!
     * This will be executed in the Threads run() method.
     *
     * @throws Exception
     */
    public void runAtStart() throws Exception {
        // Override this method when extending this Class in your thread
        if (runAtStart!=null) runAtStart.accept(this);
    }

    /**
     * Increments the current (now) value.
     * Also checks if its finished.
     *
     * @return the incremented now value.
     */
    public long step() {
        this.now++;
        isFinished();
        return now;
    }

    /**
     * Get the current values percentage (now/max).
     *
     * @return
     */
    public byte getPercent() {
        if (now == 0) return 0; // Protection against division by 0
        else return (byte) (now * 100 / max);
    }

    /**
     * Convenience method for finishing a thread, stopping the thread
     * and releasing system resources.
     * By default success is false. Remember to use
     * the setSuccess() method or by using the alternate
     * finish() method to set that value.
     */
    public void finish() {
        if (!finished){
            setNow(this.getMax());
            manager.getActive().remove(this); // Removes itself from the active threads list
            finished = true;
            interrupt();
        }
    }

    /**
     * Executes finish() but also sets the
     * success result.
     *
     * @param success Did the thread succeeded?
     */
    public void finish(boolean success) {
        finish();
        setSuccess(success);
    }

    /**
     * Finishes the thread and sets its status message.
     */
    public void finish(String status) {
        finish();
        setStatus(status);
    }

    /**
     * Finishes the thread.
     * Sets status message and success.
     */
    public void finish(String status, boolean success) {
        finish();
        setSuccess(success);
        setStatus(status);
    }

    public void skip() {
        skip(null);
    }

    /**
     * Finishes the thread.
     * Useful when using with a configuration file.
     */
    public void skip(String status) {
        skipped = true;
        finish();
        if (status != null && !status.trim().isEmpty())
            setStatus(status);
        else
            setStatus("Skipped.");
    }

    @Override
    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    @Override
    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    @Override
    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    @Override
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean isFinished() {
        if (now == max) {
            if (autoFinish) finish();
        }
        return finished;
    }

    public boolean isSkipped() {
        return skipped;
    }

    /**
     * Did the thread finish as planned or not?
     * Default is true.
     *
     * @return false if an error occurred.
     */
    @Override
    public boolean isSuccess() {
        return success;
    }

    /**
     * The default value is true.
     * When called also finishes the thread.
     *
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
        if (!finished) finish();
    }

    /**
     * If true, the {@link #runAtStart()} method gets called
     * in the constructor.
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * See {@link #isAutoStart()} for details.
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public boolean isAutoFinish() {
        return autoFinish;
    }

    public void setAutoFinish(boolean autoFinish) {
        this.autoFinish = autoFinish;
    }

    public BetterThread addWarning(String warning) {
        warnList.add(new BetterWarning(this, warning));
        return this;
    }

    public BetterThread addWarning(BetterWarning warning) {
        warnList.add(warning);
        return this;
    }

    /**
     * Same as {@link #getWarnList()}.
     */
    @Override
    public List<BetterWarning> getWarnings() {
        return warnList;
    }

    /**
     * Same as {@link #getWarnings()}.
     */
    public List<BetterWarning> getWarnList() {
        return warnList;
    }

    public void setWarnList(List<BetterWarning> warnList) {
        this.warnList = warnList;
    }

    public BetterThread addInfo(String info) {
        this.infoList.add(info);
        return this;
    }

    public List<String> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<String> infoList) {
        this.infoList = infoList;
    }

    public BetterThreadManager getManager() {
        return manager;
    }

    public void setManager(BetterThreadManager manager) {
        this.manager = manager;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

}

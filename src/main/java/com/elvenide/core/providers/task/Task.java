package com.elvenide.core.providers.task;

import com.elvenide.core.Core;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

public class Task {

    private final Core core;
    private BukkitRunnable runnable = null;
    private long executions = 0;
    private long ticksPassed = 0;
    private long tickInterval = 0;
    private Consumer<Task> consumer = t -> {
        executions++;
        ticksPassed += tickInterval;
    };

    @ApiStatus.Internal
    Task(Core core) {
        super();

        if (core == null)
            throw new IllegalArgumentException("ElvenideCore cannot be null");
        this.core = core;
    }

    /**
     * Adds an executable action to the task.
     * @param consumer The action
     * @return This
     * @since 0.0.15
     */
    @Contract(pure = true)
    public synchronized Task then(Consumer<Task> consumer) {
        this.consumer = this.consumer.andThen(consumer);
        return this;
    }

    /**
     * Cancels the task, preventing further execution.
     * @since 0.0.15
     */
    public synchronized void cancel() {
        runnable.cancel();
    }

    /**
     * Checks if the task has been cancelled.
     * @return True if cancelled
     * @since 0.0.15
     */
    public synchronized boolean isCancelled() {
        return runnable.isCancelled();
    }

    /**
     * Gets the number of times the task has been executed, including the current execution.
     * @return Number of executions
     * @since 0.0.15
     */
    public synchronized long getExecutions() {
        return executions;
    }

    /**
     * Gets the number of ticks passed since the task was first scheduled.
     * @return Number of ticks passed
     * @since 0.0.15
     */
    public synchronized long getTicksPassed() {
        return ticksPassed;
    }

    @ApiStatus.Internal
    private synchronized void setup() {
        if (core.plugin == null)
            throw new IllegalStateException("Your plugin is using ElvenideCore features that require initialization, please do so via Core.setPlugin()");

        if (runnable != null)
            runnable.cancel();

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                consumer.accept(Task.this);
            }
        };
    }

    /**
     * Repeatedly executes the task with the given delay and period.
     * @param delay Delay, in ticks
     * @param period Period, in ticks
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask repeat(long delay, long period) {
        setup();
        this.ticksPassed += delay;
        this.tickInterval = period;
        return runnable.runTaskTimer(core.plugin, delay, period);
    }

    /**
     * Executes the task with the given delay.
     * @param delay Delay, in ticks
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask delay(long delay) {
        setup();
        this.tickInterval = delay;
        return runnable.runTaskLater(core.plugin, delay);
    }

    /**
     * Repeatedly executes the task asynchronously with the given delay and period.
     * @param delay Delay, in ticks
     * @param period Period, in ticks
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask repeatAsync(long delay, long period) {
        setup();
        this.ticksPassed += delay;
        this.tickInterval = period;
        return runnable.runTaskTimerAsynchronously(core.plugin, delay, period);
    }

    /**
     * Executes the task asynchronously with the given delay.
     * @param delay Delay, in ticks
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask delayAsync(long delay) {
        setup();
        this.tickInterval = delay;
        return runnable.runTaskLaterAsynchronously(core.plugin, delay);
    }

    /**
     * Repeatedly executes the task with the given delay and period.
     * @param delay Delay, in seconds
     * @param period Period, in seconds
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask repeatSecs(double delay, double period) {
        return repeat((long) (delay * 20), (long) (period * 20));
    }

    /**
     * Executes the task with the given delay.
     * @param delay Delay, in seconds
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask delaySecs(double delay) {
        return delay((long) (delay * 20));
    }

    /**
     * Repeatedly executes the task asynchronously with the given delay and period.
     * @param delay Delay, in seconds
     * @param period Period, in seconds
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask repeatAsyncSecs(double delay, double period) {
        return repeatAsync((long) (delay * 20), (long) (period * 20));
    }

    /**
     * Executes the task asynchronously with the given delay.
     * @param delay Delay, in seconds
     * @return Bukkit task representation
     * @since 0.0.15
     */
    public synchronized BukkitTask delayAsyncSecs(double delay) {
        return delayAsync((long) (delay * 20));
    }
}

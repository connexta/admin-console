/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 **/
package org.codice.ddf.admin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accepts a list of tasks that are executed in order.
 *
 * @param <T> the expected return type of execution results
 */
public class PrioritizedBatchExecutor<T, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrioritizedBatchExecutor.class);

    private static final int MAX_THREAD_POOL_SIZE = 64;

    private final ExecutorService threadPool;

    private final List<List<Callable<T>>> tasks;

    private final Function<T, R> taskHandler;

    /**
     * Creates a new {@code PrioritizedBatchExecutor}.
     * <p>
     * Special consideration should be given when choosing the amount of threads to use. For example, tasks
     * that are computation heavy should not have a thread pool size that exceeds the number of processors available
     * to the JVM, since it can cause the JVM to slow down or run out of memory. Likewise, if the tasks are IO
     * heavy, it can be useful to use more threads than available processors since it is likely threads will spend
     * time waiting for responses to their requests.
     *
     * @param threadPoolSize size of the underlying {@code ExecutorService}. 1-64 size is valid. If an
     *                       argument higher than 64 is detected, it will default to the max number of threads.
     * @param tasks          a non-null {@code List} of tasks that will be executed in order
     * @param taskHandler    a non-null task handler that determines if a task result is valid to return
     */
    public PrioritizedBatchExecutor(int threadPoolSize, List<List<Callable<T>>> tasks,
            Function<T, R> taskHandler) {
        Validate.notNull(tasks, "Argument {tasks} cannot be null.");
        Validate.notNull(taskHandler, "Argument {taskHandler} cannot be null.");

        if (threadPoolSize > MAX_THREAD_POOL_SIZE) {
            LOGGER.debug(
                    "Argument {threadPoolSize} with value [{}] exceeds maximum allowed value, defaulting to the max number of threads [{}].",
                    threadPoolSize,
                    MAX_THREAD_POOL_SIZE);

            threadPoolSize = MAX_THREAD_POOL_SIZE;
        }

        this.tasks = tasks;
        this.taskHandler = taskHandler;

        threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    /**
     * Start task execution and blocks until the highest priority task batch has returned a valid result
     * according to the task handler, then cleans up remaining tasks. The current instance of the
     * {@code PrioritizedBatchExecutor} is not useable after calling {@code getFirst()}.
     *
     * @return an {@code Optional} containing a task's result, if there was one
     */
    public Optional<R> getFirst() {
        try {
            List<CompletionService<T>> prioritizedCompletionServices =
                    getPrioritizedCompletionServices();

            for (int i = 0; i < tasks.size(); i++) {
                List<Callable<T>> taskBatch = tasks.get(i);
                CompletionService<T> completionService = prioritizedCompletionServices.get(i);

                LOGGER.debug("Executing batch {} of {}.", i + 1, tasks.size());
                for (int j = 0; j < taskBatch.size(); j++) {
                    try {
                        LOGGER.debug("\tAttempting to take from completion service queue.");
                        Future<T> taskFuture = completionService.take();

                        Optional<R> result = handleTaskResult(taskFuture);
                        if (result.isPresent()) {
                            LOGGER.debug("\tReturning valid task result {} of {} tasks.",
                                    j + 1,
                                    taskBatch.size());
                            return result;
                        }
                    } catch (InterruptedException e) {
                        LOGGER.debug("\t\tFailed to get future from completion service.");
                        Thread.currentThread()
                                .interrupt();
                    }

                    LOGGER.debug("\tReceived invalid task result {} of {} tasks.",
                            j + 1,
                            taskBatch.size());
                }
                LOGGER.debug("\tFound no valid results in batch {}.", i + 1);
            }

            LOGGER.debug("Found no valid responses in all {} batches.", tasks.size());
            return Optional.empty();
        } finally {
            cleanUp();
        }
    }

    /**
     * Start task execution and blocks until the highest priority task batch has returned a valid result
     * according to the task handler, then cleans up remaining tasks. The current instance of the
     * {@code PrioritizedBatchExecutor} is not useable after calling {@code getFirst(long, TimeUnit)}.
     *
     * @param batchWaitTime amount of time to wait for each batch execution.
     * @param timeUnit      {@code TimeUnit} to use for the {@code batchWaitTime}
     * @return an {@code Optional} containing a task's result, if there was one
     */
    public Optional<R> getFirst(long batchWaitTime, TimeUnit timeUnit) {
        if (batchWaitTime < 0) {
            throw new IllegalArgumentException("Batch wait time must be 0 or greater.");
        }

        if (batchWaitTime == 0) {
            return getFirst();
        }

        Validate.notNull(timeUnit,
                "Argument {timeUnit} cannot be null when argument {batchWaitTime} is above 0.");

        long batchWaitTimeInMillis = TimeUnit.MILLISECONDS.convert(batchWaitTime, timeUnit);

        try {
            List<CompletionService<T>> prioritizedCompletionServices =
                    getPrioritizedCompletionServices();

            for (int i = 0; i < tasks.size(); i++) {
                List<Callable<T>> taskBatch = tasks.get(i);
                CompletionService<T> completionService = prioritizedCompletionServices.get(i);

                long batchStartTime = System.currentTimeMillis();
                long batchEndTime = batchStartTime + batchWaitTimeInMillis;
                long lastPollTime = batchStartTime;
                LOGGER.debug("Executing batch {} of {}.", i + 1, tasks.size());
                for (int j = 0; j < taskBatch.size(); j++) {
                    try {
                        long pollTime = batchEndTime - lastPollTime;
                        Future<T> taskFuture;
                        if (pollTime > 0) {
                            LOGGER.debug("\tPolling task result queue for {} milliseconds.",
                                    pollTime);
                            taskFuture = completionService.poll(pollTime, TimeUnit.MILLISECONDS);
                            lastPollTime = System.currentTimeMillis();
                        } else {
                            LOGGER.debug(
                                    "\tWaited {} {} for batch results. Skipping remaining tasks in batch {}.",
                                    batchWaitTime,
                                    timeUnit.toString(),
                                    i + 1);
                            break;
                        }

                        Optional<R> result = handleTaskResult(taskFuture);
                        if (result.isPresent()) {
                            LOGGER.debug("\tReturning valid task result {} of {} tasks.",
                                    j + 1,
                                    taskBatch.size());
                            return result;
                        }
                    } catch (InterruptedException e) {
                        LOGGER.debug("\t\tFailed to get future from completion service.");
                        Thread.currentThread()
                                .interrupt();
                    }

                    LOGGER.debug("\tReceived invalid task result {} of {} tasks.",
                            j + 1,
                            taskBatch.size());
                }
            }

            LOGGER.debug("Found no valid responses in all {} batches.", tasks.size());
            return Optional.empty();
        } finally {
            cleanUp();
        }
    }

    private List<CompletionService<T>> getPrioritizedCompletionServices() {
        List<CompletionService<T>> prioritizedCompletionServices = new ArrayList<>();

        for (List<Callable<T>> taskBatch : tasks) {
            CompletionService<T> completionService = new ExecutorCompletionService<>(threadPool);

            for (Callable<T> task : taskBatch) {
                completionService.submit(task);
            }

            prioritizedCompletionServices.add(completionService);
        }

        return prioritizedCompletionServices;
    }

    private Optional<R> handleTaskResult(Future<T> future) {
        try {
            R result;
            if (future != null && (result = taskHandler.apply(future.get())) != null) {
                return Optional.of(result);
            }
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.debug("\t\tFailed to get task result from future.");
        }

        return Optional.empty();
    }

    private void cleanUp() {
        LOGGER.debug("Shutting down ExecutionService.");
        threadPool.shutdownNow();
    }
}

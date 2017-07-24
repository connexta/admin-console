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
package org.codice.ddf.admin.common

import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Function

class PrioritizedBatchExecutorSpec extends Specification {

    static EXPECTED_RESULT = 'expectedResult'

    static NOT_EXPECTED_RESULT = 'notExpectedResult'

    PrioritizedBatchExecutor prioritizedBatchExecutor

    def 'Greater than max number of threads defaults to max numbers of threads'() {
        when:
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(65, [], createTaskHandler(['foobar']))

        then:
        ((ThreadPoolExecutor) prioritizedBatchExecutor.threadPool).getCorePoolSize() == PrioritizedBatchExecutor.MAX_THREAD_POOL_SIZE
    }

    def '0 or less threadPoolSize throws IllegalArgumentException'() {
        when:
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(0, [], createTaskHandler(['foobar']))

        then:
        thrown(IllegalArgumentException)
    }

    def 'Null task list throws IllegalArgumentException'() {
        when:
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(1, null, createTaskHandler(['foobar']))

        then:
        thrown(IllegalArgumentException)
    }

    def 'Null task handler throws IllegalArgumentException'() {
        when:
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(1, [], null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'Result is successfully retrieved from single batch'() {
        setup:
        def taskResults = [[NOT_EXPECTED_RESULT, EXPECTED_RESULT]]
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(8, createTaskList(taskResults), createTaskHandler([EXPECTED_RESULT]))

        when:
        def result = prioritizedBatchExecutor.getFirst()

        then:
        result.isPresent()
        result.get() == EXPECTED_RESULT
    }

    def 'Result is successfully retrieved from 2nd of 2 batches'() {
        setup:
        def taskResults = [
                [NOT_EXPECTED_RESULT, NOT_EXPECTED_RESULT],
                [NOT_EXPECTED_RESULT, EXPECTED_RESULT]
        ]
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(8, createTaskList(taskResults), createTaskHandler([EXPECTED_RESULT]))

        when:
        def result = prioritizedBatchExecutor.getFirst()

        then:
        result.isPresent()
        result.get()
    }

    def 'No results from many batches returns empty optional'() {
        setup:
        def taskResults = [
                [NOT_EXPECTED_RESULT, NOT_EXPECTED_RESULT],
                [NOT_EXPECTED_RESULT, NOT_EXPECTED_RESULT]
        ]
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(8, createTaskList(taskResults), createTaskHandler([EXPECTED_RESULT]))

        when:
        def result = prioritizedBatchExecutor.getFirst()

        then:
        !result.isPresent()
    }

    def 'Batch wait time is exceeded for first batch'() {
        setup:
        def batch1 = []
        def task1 = createTask(EXPECTED_RESULT, 500)
        batch1.add(task1)

        def batch2 = []
        def task2 = createTask('anotherExpectedResult')
        batch2.add(task2)

        def taskList = []
        taskList.add(batch1)
        taskList.add(batch2)

        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(8, taskList, createTaskHandler([EXPECTED_RESULT, 'anotherExpectedResult']))


        when:
        def result = prioritizedBatchExecutor.getFirst(250, TimeUnit.MILLISECONDS)

        then:
        result.isPresent()
        result.get() == 'anotherExpectedResult'
    }

    def 'No result returned due to batch wait time being exceeded for all batches'() {
        setup:
        def batch1 = []
        def task1 = createTask(EXPECTED_RESULT, 500)
        batch1.add(task1)

        def batch2 = []
        def task2 = createTask(EXPECTED_RESULT, 500)
        batch2.add(task2)

        def taskList = []
        taskList.add(batch1)
        taskList.add(batch2)

        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(8, taskList, createTaskHandler([EXPECTED_RESULT]))

        when:
        def result = prioritizedBatchExecutor.getFirst(100, TimeUnit.MILLISECONDS)

        then:
        !result.isPresent()
    }

    def 'IllegalArgumentException when batch wait time is negative'() {
        setup:
        def taskResults = [[NOT_EXPECTED_RESULT, NOT_EXPECTED_RESULT]]
        prioritizedBatchExecutor = new PrioritizedBatchExecutor<String>(8, createTaskList(taskResults), createTaskHandler([EXPECTED_RESULT]))

        when:
        prioritizedBatchExecutor.getFirst(-1, TimeUnit.MILLISECONDS)

        then:
        thrown(IllegalArgumentException)
    }

    def createTaskList(List<List<String>> taskResults, long sleepTimeInMillis = 0) {
        List<List<Callable<String>>> taskList = []

        for (List<String> taskBatchResults : taskResults) {
            List<Callable<String>> batchResults = []
            for (String taskResult : taskBatchResults) {
                batchResults.add(createTask(taskResult, sleepTimeInMillis))
            }
            taskList.add(batchResults)
        }

        return taskList
    }

    def createTask(String taskResult, long sleepTimeInMillis = 0) {
        def taskCallable = new Callable<String>() {
            @Override
            String call() throws Exception {
                Thread.sleep(sleepTimeInMillis)
                return taskResult
            }
        }
        return taskCallable
    }

    def createTaskHandler(List<String> expectedResults) {
        return new Function<String, String>() {
            @Override
            String apply(String o) {
                return expectedResults.contains(o) ? o : null
            }
        }
    }
}

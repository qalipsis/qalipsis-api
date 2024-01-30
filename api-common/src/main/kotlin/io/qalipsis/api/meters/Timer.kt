/*
 * Copyright 2023 AERIS IT Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package io.qalipsis.api.meters

import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * Meter intended to track a large number of short running events.
 *
 * @author Francisca Eze
 */
interface Timer : Meter<Timer> {
    /**
     * Updates the statistics kept by the timer with the specified amount.
     * @param amount Duration of a single event being measured by this timer. If the
     * amount is less than 0 the value will be dropped.
     * @param unit Time unit for the amount being recorded.
     */
    fun record(amount: Long, unit: TimeUnit?)

    /**
     * Updates the statistics kept by the timer with the specified amount.
     * @param duration Duration of a single event being measured by this timer.
     */
    fun record(duration: Duration) {
        record(duration.toNanos(), TimeUnit.NANOSECONDS)
    }

    /**
     * Executes the Supplier `f` and records the time taken.
     * @param f Function to execute and measure the execution time.
     * @param <T> The return type of the [Supplier].
     * @return The return value of `f`.
    </T> */
    suspend fun <T> record(block: suspend () -> T): T

    /**
     * @return The number of times that stop has been called on this timer.
     */
    fun count(): Long

    /**
     * @param unit The base unit of time to scale the total to.
     * @return The total time of recorded events.
     */
    fun totalTime(unit: TimeUnit?): Double

    /**
     * @param unit The base unit of time to scale the mean to.
     * @return The distribution average for all recorded events.
     */
    fun mean(unit: TimeUnit?): Double {
        val count = count()
        return if (count == 0L) 0.0 else totalTime(unit) / count
    }

    /**
     * @param unit The base unit of time to scale the max to.
     * @return The maximum time of a single event.
     */
    fun max(unit: TimeUnit?): Double

    /**
     * Provides cumulative histogram counts.
     * @param valueNanos The histogram bucket to retrieve a count for.
     * @return The count of all events less than or equal to the bucket. If valueNanos
     * does not match a preconfigured bucket boundary, returns NaN.
     */
    fun histogramCountAtValue(valueNanos: Long): Double

    /**
     * @param percentile A percentile in the domain [0, 1]. For example, 0.5 represents
     * the 50th percentile of the distribution.
     * @param unit The base unit of time to scale the percentile value to.
     * @return The latency at a specific percentile. This value is non-aggregable across
     * dimensions. Returns NaN if percentile is not a preconfigured percentile that
     * Micrometer is tracking.
     */
    fun percentile(percentile: Double, unit: TimeUnit?): Double
}
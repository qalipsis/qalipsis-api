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

/**
 *
 */
interface DistributionSummary : Meter<DistributionSummary> {
    /**
     * Updates the statistics kept by the summary with the specified amount.
     * @param amount Amount for an event being measured. For example, if the size in bytes
     * of responses from a server. If the amount is less than 0 the value will be dropped.
     */
    fun record(amount: Double)

    /**
     * @return The number of times that record has been called since this timer was
     * created.
     */
    fun count(): Long

    /**
     * @return The total amount of all recorded events.
     */
    fun totalAmount(): Double

    /**
     * @return The distribution average for all recorded events.
     */
    fun mean(): Double {
        val count = count()
        return if (count == 0L) 0.0 else totalAmount() / count
    }

    /**
     * @return The maximum time of a single event.
     */
    fun max(): Double

    /**
     * Provides cumulative histogram counts.
     * @param value The histogram bucket to retrieve a count for.
     * @return The count of all events less than or equal to the bucket. If value does not
     * match a preconfigured bucket boundary, returns NaN.
     */
    @Deprecated("Use {@link #takeSnapshot()} to retrieve bucket counts.")
    fun histogramCountAtValue(value: Long): Double

    /**
     * @param percentile A percentile in the domain [0, 1]. For example, 0.5 represents
     * the 50th percentile of the distribution.
     * @return The latency at a specific percentile. This value is non-aggregable across
     * dimensions. Returns NaN if percentile is not a preconfigured percentile that
     * Micrometer is tracking.
     */
    @Deprecated("Use {@link #takeSnapshot()} to retrieve percentiles.")
    fun percentile(percentile: Double): Double

}
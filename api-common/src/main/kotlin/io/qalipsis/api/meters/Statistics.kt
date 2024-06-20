/*
 * Copyright 2024 AERIS IT Solutions GmbH
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
 * Meter to track the global statistics of observations.
 *
 * @author Francisca Eze
 */
interface Statistics : Meter<Statistics> {

    /**
     * Updates the statistics meter with the specified amount.
     *
     * @param amount amount for an event being measured. If the amount is less than 0 the value will be dropped.
     */
    fun record(amount: Double)

    /**
     * Returns the number of times that record has been called since this meter was
     * created.
     */
    fun count(): Long

    /**
     * Returns the total sum of the recorded observations.
     */
    fun totalAmount(): Double

    /**
     * Returns the distribution average of the recorded observations.
     */
    fun mean(): Double

    /**
     * Returns the max value from the observations recorded.
     */
    fun max(): Double

    /**
     * Specifies a percentile in the domain. It expresses the point where an observation falls within a given
     * range of other observations.
     *
     * @param percentile the percentage point to be observed
     */
    fun percentile(percentile: Double): Double
}
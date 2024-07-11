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
 * Measures the relationship between two independently tracked [Gauge] metrics.
 *
 * @author Francisca Eze
 */
interface Rate : Meter<Rate> {

    /**
     * Calculates an instantaneous result gotten from dividing two gauge meters.
     */
    fun current(): Double {
        return Double.NaN
    }

    /**
     * Decrease the value of the primary gauge by the `amount`.
     *
     * @param amount amount to subtract from the gauge value
     */
    fun decrementPrimaryMetric(amount: Double = 1.0): Double

    /**
     * Increase the value of the primary gauge by the `amount`.
     *
     * @param amount amount to add to the gauge value
     */
    fun incrementPrimaryMetric(amount: Double = 1.0): Double

    /**
     * Decrease the value of the secondary gauge by the `amount`.
     *
     * @param amount amount to subtract from the gauge value
     */
    fun decrementSecondaryMetric(amount: Double = 1.0): Double

    /**
     * Increase the value of the secondary gauge by the `amount`.
     *
     * @param amount amount to add to the gauge value
     */
    fun incrementSecondaryMetric(amount: Double = 1.0): Double
}

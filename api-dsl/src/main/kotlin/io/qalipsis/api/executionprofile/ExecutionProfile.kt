/*
 * Copyright 2022 AERIS IT Solutions GmbH
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

package io.qalipsis.api.executionprofile

import java.time.Duration

/**
 *
 * [ExecutionProfile] is an accessor to a [ExecutionProfileIterator]. The [ExecutionProfile] is part of the definition of the
 * scenario and defines the pace to start the minions.
 *
 * @author Eric Jessé
 */
interface ExecutionProfile {

    /**
     * Notifies the execution profile that the campaign is starting.
     */
    fun notifyStart(speedFactor: Double) = Unit

    /**
     * Generates a new [ExecutionProfileIterator] to define a new sequence of starts.
     *
     * @param totalMinionsCount the total number of minions that will be started for the scenario.
     * @param speedFactor the factor to accelerate (when greater than 1) or slower (between 0 and 1) the ramp-up.
     */
    fun iterator(totalMinionsCount: Int, speedFactor: Double): ExecutionProfileIterator

    /**
     * Verifies whether the completed minion, can be restarted.
     *
     * @param minionExecutionDuration the total duration the minion required to execute the full scenario
     */
    fun canReplay(minionExecutionDuration: Duration): Boolean = false
}

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

package io.qalipsis.api.rampup

/**
 * [RampUpStrategyIterator] defines how fast the [io.qalipsis.api.orchestration.Minion]s has to be started to simulate the load
 * on a scenario.
 *
 * @author Eric Jessé
 */
interface RampUpStrategyIterator {

    /**
     * Defines the next starting line for the strategy.
     */
    fun next(): MinionsStartingLine
}

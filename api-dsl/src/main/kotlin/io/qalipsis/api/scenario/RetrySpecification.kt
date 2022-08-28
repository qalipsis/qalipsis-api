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

package io.qalipsis.api.scenario

import io.qalipsis.api.retry.RetryPolicy

/**
 * Interface of a specification supporting the configuration of a retry policy.
 *
 * @author Eric Jessé
 */
interface RetrySpecification {

    /**
     * Defines the default retry strategy for all the steps of the scenario.
     * The strategy can be redefined individually for each step.
     */
    fun retryPolicy(retryPolicy: RetryPolicy)
}
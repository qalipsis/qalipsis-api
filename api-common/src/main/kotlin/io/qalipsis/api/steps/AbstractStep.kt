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

package io.qalipsis.api.steps

import io.qalipsis.api.context.StepName
import io.qalipsis.api.retry.RetryPolicy

/**
 * Simple super class of steps in order to perform generic operations without redundancy.
 *
 * @author Eric Jessé
 */
abstract class AbstractStep<I, O>(override val name: StepName, override var retryPolicy: RetryPolicy?) : Step<I, O> {

    override val next: MutableList<Step<O, *>> = mutableListOf()

    override fun addNext(nextStep: Step<*, *>) {
        @Suppress("UNCHECKED_CAST")
        next.add(nextStep as Step<O, *>)
    }
}

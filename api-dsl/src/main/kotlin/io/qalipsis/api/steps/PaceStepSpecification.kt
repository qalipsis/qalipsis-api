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

import io.micronaut.core.annotation.Introspected
import java.time.Duration

/**
 * Specification for a [io.qalipsis.core.factory.steps.PaceStep].
 *
 * @author Eric Jessé
 */
@Introspected
data class PaceStepSpecification<INPUT>(
    val specification: (pastPeriodMs: Long) -> Long
) : AbstractStepSpecification<INPUT, INPUT, PaceStepSpecification<INPUT>>()

/**
 * Forces the records to be forwarded to the next step with the given pace. The pace calculation is isolated
 * for each minion.
 *
 * @param specification the calculation of the pace, considering the previous one (which is 0 at start).
 *
 * @author Eric Jessé
 */
fun <INPUT> StepSpecification<*, INPUT, *>.pace(
    specification: (pastPeriodMs: Long) -> Long
): PaceStepSpecification<INPUT> {
    val step = PaceStepSpecification<INPUT>(specification)
    this.add(step)
    return step
}

/**
 * Forces the records to be forwarded to the next step with a constant pace. The pace calculation is isolated
 * for each minion.
 *
 * @param durationInMs the constant delay between two forwarding of record to the next step, in milliseconds.
 *
 * @author Eric Jessé
 */
fun <INPUT> StepSpecification<*, INPUT, *>.constantPace(durationInMs: Long): PaceStepSpecification<INPUT> {
    return pace { _ -> durationInMs }
}

/**
 * Forces the records to be forwarded to the next step with a constant pace. The pace calculation is isolated
 * for each minion.
 *
 * @param duration the constant delay between two forwarding of record to the next step.
 *
 * @author Eric Jessé
 */
fun <INPUT> StepSpecification<*, INPUT, *>.constantPace(duration: Duration): PaceStepSpecification<INPUT> {
    return pace { _ -> duration.toMillis() }
}

/**
 * Forces the records to be forwarded to the next step, accelerating the pace at each record. The pace calculation is isolated
 * for each minion.
 *
 * @param startPeriodMs the duration to apply between the first and the second record, in milliseconds.
 * @param accelerator the acceleration factor to divide the previous period: use a value greater than 1 to accelerate, between 0 and 1 to go lower.
 * @param minPeriodMs the minimal period limit in milliseconds.
 *
 * @author Eric Jessé
 */
fun <INPUT> StepSpecification<*, INPUT, *>.acceleratingPace(
    startPeriodMs: Long, accelerator: Double,
    minPeriodMs: Long
): PaceStepSpecification<INPUT> {
    // Secure the accelerator to avoid divide by 0 and negative values.
    val actualAccelerator = 1 / accelerator.coerceAtLeast(10E-12)
    return pace { pastPeriodMs ->
        if (pastPeriodMs == 0L) {
            startPeriodMs
        } else {
            (pastPeriodMs * actualAccelerator).toLong().coerceAtLeast(minPeriodMs)
        }
    }
}

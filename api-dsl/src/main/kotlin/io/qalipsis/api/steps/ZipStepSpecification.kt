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

import cool.graph.cuid.Cuid
import io.micronaut.core.annotation.Introspected
import io.qalipsis.api.context.CorrelationRecord
import io.qalipsis.api.context.StepName
import io.qalipsis.api.scenario.ScenarioSpecification
import javax.validation.constraints.NotBlank

/**
 * Specification for a [io.qalipsis.core.factory.steps.ZipStep].
 *
 * @author Polina Bril
 */
@Introspected
data class ZipStepSpecification<INPUT, OUTPUT>(
    val secondaryStepName: @NotBlank StepName
) : AbstractStepSpecification<INPUT?, OUTPUT?, ZipStepSpecification<INPUT, OUTPUT>>()

/**
 * Joins parallel sources that are not correlated.
 *
 * @param on specification of the step that should generate the remote records.
 */
fun <INPUT, OTHER_INPUT> StepSpecification<*, INPUT, *>.zip(
    on: (scenario: ScenarioSpecification) -> StepSpecification<*, OTHER_INPUT, *>
): ZipStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>> {

    // Since the relationship is performed in the step name, we generate one in case it is not specified by the user.
    val secondaryStep = on(scenario)
    if (secondaryStep.name.isBlank()) {
        secondaryStep.name = Cuid.createCuid()
    }
    // We force the step to be known by the scenario.
    scenario.register(secondaryStep)

    @Suppress("UNCHECKED_CAST")
    val step = ZipStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>>(secondaryStep.name)
    this.add(step)
    return step
}

/**
 * Variant of [zip] to use for auto-completion in IDEs. Once the scenario compile with this,
 * replace the operator by [zip] to have a strongly types output.
 */
fun <INPUT> StepSpecification<*, INPUT, *>.zipUncasted(on: (scenario: ScenarioSpecification) -> StepSpecification<*, Any?, *>) =
    this.zip(on = on)

/**
 * Joins parallel sources that are not correlated.
 *
 * @param otherInput defines the other input type from remote records.
 * @param on name of the step, which should have be specified also in the same scenario.
 */
fun <INPUT, OTHER_INPUT> StepSpecification<*, INPUT, *>.zip(
    on: String,
    otherInput: CorrelationRecord<OTHER_INPUT>? = null//otherInput for tests with DummyStepSpecification where OTHER_INPUT is not specified.
): ZipStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>> {

    @Suppress("UNCHECKED_CAST")
    val step = ZipStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>>(on)
    this.add(step)
    return step
}
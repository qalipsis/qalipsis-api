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

import io.qalipsis.api.context.StepContext
import io.qalipsis.api.scenario.StepSpecificationRegistry
import io.qalipsis.api.scenario.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author Eric Jessé
 */
internal class SimpleStepSpecificationTest {

    @Test
    internal fun `should add simple step as next`() {
        val previousStep = DummyStepSpecification()
        val specification: suspend (context: StepContext<Int, String>) -> Unit = { _ -> }
        previousStep.execute(specification)

        assertEquals(SimpleStepSpecification(specification), previousStep.nextSteps[0])
    }

    @Test
    internal fun `should add simple step to scenario`() {
        val scenario = scenario("my-scenario") as StepSpecificationRegistry
        val specification: suspend (context: StepContext<Unit, String>) -> Unit = { _ -> }
        scenario.execute(specification)

        assertEquals(SimpleStepSpecification(specification), scenario.rootSteps[0])
    }

}

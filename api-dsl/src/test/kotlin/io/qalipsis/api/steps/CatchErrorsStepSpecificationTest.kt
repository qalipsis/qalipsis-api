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

import io.aerisconsulting.catadioptre.getProperty
import io.mockk.every
import io.mockk.verifyOrder
import io.qalipsis.api.context.StepError
import io.qalipsis.test.mockk.WithMockk
import io.qalipsis.test.mockk.relaxedMockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.Logger

/**
 * @author Eric Jessé
 */
@WithMockk
internal class CatchErrorsStepSpecificationTest {

    @Test
    internal fun `should add error catcher as next`() {
        val previousStep = DummyStepSpecification()
        val specification: (error: Collection<StepError>) -> Unit = { _ -> }
        previousStep.catchErrors(specification)

        assertEquals(CatchErrorsStepSpecification<Int>(specification), previousStep.nextSteps[0])
    }

    @Test
    internal fun `should add error logger as next`() {
        val previousStep = DummyStepSpecification()
        val logger = relaxedMockk<Logger> { }
        previousStep.logErrors(logger)

        assertTrue(previousStep.nextSteps[0] is CatchErrorsStepSpecification<*>)
        val specification = previousStep.nextSteps[0].getProperty<(Collection<StepError>) -> Unit>("block")
        val exceptions = listOf<Exception>(relaxedMockk {
            every { message } returns "Message-1"
        }, relaxedMockk {
            every { message } returns "Message-2"
        })
        val errors = exceptions.map { StepError(it) }
        specification(errors)
        verifyOrder {
            logger.error(eq("Message-1"))
            logger.error(eq("Message-2"))
        }
    }

}
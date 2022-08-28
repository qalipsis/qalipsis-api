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

package io.qalipsis.test.steps

import io.micrometer.core.instrument.MeterRegistry
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import io.qalipsis.api.events.EventsLogger
import io.qalipsis.api.lang.IdGenerator
import io.qalipsis.api.report.CampaignReportLiveStateRegistry
import io.qalipsis.api.retry.RetryPolicy
import io.qalipsis.api.runtime.DirectedAcyclicGraph
import io.qalipsis.api.scenario.StepSpecificationRegistry
import io.qalipsis.api.steps.StepSpecificationConverter
import io.qalipsis.test.coroutines.TestDispatcherProvider
import io.qalipsis.test.lang.TestIdGenerator
import io.qalipsis.test.mockk.WithMockk
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@WithMockk
abstract class AbstractStepSpecificationConverterTest<T : StepSpecificationConverter<*>> {

    @RelaxedMockK
    lateinit var meterRegistry: MeterRegistry

    @RelaxedMockK
    lateinit var eventsLogger: EventsLogger

    @RelaxedMockK
    lateinit var scenarioSpecification: StepSpecificationRegistry

    @RelaxedMockK
    lateinit var directedAcyclicGraph: DirectedAcyclicGraph

    @RelaxedMockK
    lateinit var mockedRetryPolicy: RetryPolicy

    @RelaxedMockK
    lateinit var campaignReportLiveStateRegistry: CampaignReportLiveStateRegistry

    @SpyK
    var idGenerator: IdGenerator = TestIdGenerator

    @RelaxedMockK
    lateinit var campaignCoroutineScope: CoroutineScope

    @JvmField
    @RegisterExtension
    val testCoroutineDispatcher = TestDispatcherProvider()

    @InjectMockKs
    lateinit var converter: T

    @Test
    abstract fun `should support expected spec`()

    @Test
    abstract fun `should not support unexpected spec`()
}

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

import io.qalipsis.api.context.DirectedAcyclicGraphName
import io.qalipsis.api.steps.StepSpecification

/**
 * Wrapper of a scenario to introduce the beginning of the "loaded tree": the subpart of the scenario, which will
 * received the load of the minions.
 *
 * @author Eric Jessé
 */
internal class StartScenarioSpecificationWrapper(
    private val delegated: StepSpecificationRegistry,
    private val dagId: DirectedAcyclicGraphName
) : StepSpecificationRegistry by delegated {

    override fun add(step: StepSpecification<*, *, *>) {
        step.directedAcyclicGraphName = dagId
        delegated.add(step)
    }

    override fun insertRoot(newRoot: StepSpecification<*, *, *>, rootToShift: StepSpecification<*, *, *>) {
        newRoot.directedAcyclicGraphName = dagId
        delegated.insertRoot(newRoot, rootToShift)
    }
}

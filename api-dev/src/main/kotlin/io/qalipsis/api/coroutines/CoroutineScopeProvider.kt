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

package io.qalipsis.api.coroutines

import kotlinx.coroutines.CoroutineScope

/**
 * Provider of [CoroutineScope] for the different kinds of operations in QALIPSIS.
 *
 * @author Eric Jessé
 */
interface CoroutineScopeProvider {

    /**
     * Scope for global operations.
     */
    val global: CoroutineScope

    /**
     * Scope for execution of the scenarios.
     */
    val campaign: CoroutineScope

    /**
     * Scope for execution of the network operations.
     */
    val io: CoroutineScope

    /**
     * Scope for the background tasks.
     */
    val background: CoroutineScope

    /**
     * Scope for the orchestration tasks.
     */
    val orchestration: CoroutineScope

    fun close()
}
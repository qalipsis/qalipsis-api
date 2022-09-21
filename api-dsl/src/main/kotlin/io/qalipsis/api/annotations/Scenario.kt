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

package io.qalipsis.api.annotations

/**
 * Annotation to mark a method as a scenario specification. The function will be executed at startup
 * of the factory to load the specification. The method does not have to return something. Any returned value
 * will be ignored.
 *
 * <code>
 * @Scenario
 * fun createMyScenario() {
 *   scenario("my-scenario) {
 *          // Configure you scenario here.
 *      }
 *      // Then add steps.
 *      .justDo { context ->
 *          // ...
 *      }
 * }
 * <code>
 *
 * @author Eric Jessé
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Scenario(

    /**
     * Unique identifier or the scenario, should be kebab-cased, ex: `this-is-my-scenario`.
     */
    val name: String = "",

    /**
     * Display name or user-friendly description of the scenario, defaults to an empty string.
     */
    val description: String = "",

    /**
     * Version of the scenario, should be a dot-separated version, defaults to `0.<compilation-instant>`.
     */
    val version: String = ""

)
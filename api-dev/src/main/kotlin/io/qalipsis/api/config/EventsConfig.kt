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

package io.qalipsis.api.config

/**
 * Configuration facilities for the generation of events.
 *
 * @author Eric Jessé
 */
object EventsConfig {

    const val EXPORT_CONFIGURATION = "events.export"

    const val EXPORT_ENABLED = "$EXPORT_CONFIGURATION.enabled"

    const val PROVIDER_CONFIGURATION = "events.provider"

}
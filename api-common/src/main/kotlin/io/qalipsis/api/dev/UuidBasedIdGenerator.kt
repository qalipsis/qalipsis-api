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

package io.qalipsis.api.dev

import io.qalipsis.api.lang.IdGenerator
import jakarta.inject.Singleton
import java.util.UUID

/**
 * Implementation of [IdGenerator] using random [UUID]s.
 *
 * @author Eric Jessé
 */
@Singleton
class UuidBasedIdGenerator : IdGenerator {

    override fun long(): String {
        return UUID.randomUUID().toString().lowercase().replace("-", "")
    }

    override fun short(): String {
        return long().substring(22, 32)
    }

}

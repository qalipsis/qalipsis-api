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

package io.qalipsis.api.serialization

import io.qalipsis.api.serialization.Serializable.Format.AUTO
import javax.validation.constraints.NotEmpty
import kotlin.reflect.KClass

/**
 * Annotation to set on classes or files to trigger the creation of a QALIPSIS serialization wrapper, for types
 * supporting the native kotlin serialization, but compiled in third-parties libraries.
 *
 * Classes annotated with the [kotlinx.serialization.Serializable] and compiled with the QALIPSIS processors
 * library in the Kapt classpath do not need to additionally support [Serializable].
 *
 * See [the official documentation](https://kotlinlang.org/docs/serialization.html) for more details.
 *
 * @property types types for which a QALIPSIS serialization wrapper should be created, they should not have generic types
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
annotation class Serializable(

    @get:NotEmpty
    val types: Array<KClass<*>>,

    /**
     * Default format for the class
     */
    val format: Format = AUTO

) {
    enum class Format {
        /**
         * Uses the best (from performance perspective) serializer present in the class path.
         */
        AUTO,

        /**
         * Uses the JSON serializer.
         */
        JSON,

        /**
         * Uses the protobuf serializer.
         */
        PROTOBUF
    }
}

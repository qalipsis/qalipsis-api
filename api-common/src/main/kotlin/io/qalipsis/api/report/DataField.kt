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

package io.qalipsis.api.report

/**
 * Field of a time-series data source.
 *
 * @author Eric Jessé
 *
 * @property name name of the field in the data source
 * @property type type for the
 * @property unit the unit of the values, if relevant (durations,...) and not specified in the records
 */
data class DataField(
    val name: String,
    val type: DataFieldType,
    val unit: String? = null
)

/**
 * Type of a field of time-series data.
 */
enum class DataFieldType {
    STRING, NUMBER, BOOLEAN, OBJECT, DATE
}

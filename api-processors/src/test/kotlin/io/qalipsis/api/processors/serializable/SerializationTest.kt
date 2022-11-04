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

package io.qalipsis.api.processors.serializable

import assertk.assertThat
import assertk.assertions.containsAll
import assertk.assertions.isDataClassEqualTo
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import io.qalipsis.api.serialization.JsonSerialFormatWrapper
import io.qalipsis.api.serialization.ProtobufSerialFormatWrapper
import io.qalipsis.api.serialization.SerialFormatWrapper
import io.qalipsis.api.serialization.Serializable.Format.JSON
import io.qalipsis.api.serialization.SerializablePerson
import io.qalipsis.api.serialization.SerializablePersonSerializationWrapper
import io.qalipsis.api.serialization.SerializersProvider
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test

@Serializable
data class SerializableTeam(
    val name: String,
    val members: List<SerializablePerson>
)

@ExperimentalSerializationApi
@io.qalipsis.api.serialization.Serializable([SerializablePerson::class], format = JSON)
internal class SerializationTest {

    private val serializerProvider = SerializersProvider()

    private val personSerializer: SerialFormatWrapper<SerializablePerson> =
        serializerProvider.forType(SerializablePerson::class).first()

    private val teamSerializer: SerialFormatWrapper<SerializableTeam> =
        serializerProvider.forType(SerializableTeam::class).first()

    @Test
    internal fun `should load all the serializers classes`() {
        val classes = serializerProvider.serialFormatWrappers

        assertThat(classes.map { it::class }).containsAll(
            SerializableTeamSerializationWrapper::class,
            SerializablePersonSerializationWrapper::class
        )
    }

    @Test
    internal fun `default format should be protobuf when present`() {
        assertThat(teamSerializer).isInstanceOf(ProtobufSerialFormatWrapper::class)
    }

    @Test
    internal fun `used format should be JSON when specified`() {
        assertThat(personSerializer).isInstanceOf(JsonSerialFormatWrapper::class)
    }

    @Test
    internal fun `should serialize and deserialize a local type with the default format`() {
        // given
        val serializable = SerializableTeam(
            "development",
            listOf(SerializablePerson("alice", 38), SerializablePerson("bob", 37))
        )

        // when
        val serialized = teamSerializer.serialize(serializable)
        val deserialized = teamSerializer.deserialize(serialized)

        // then
        assertThat(deserialized).isDataClassEqualTo(serializable)
    }

    @Test
    internal fun `should deserialize a serialized type from a dependency`() {
        // given
        val serializable = SerializablePerson("alice", 38)

        // when
        val result = personSerializer.serialize(serializable)

        // then
        assertThat(result.decodeToString()).isEqualTo("""{"name":"alice","age":38}""")
    }

    @Test
    internal fun `should serialize a serialized type from a dependency`() {
        // given
        val source = """{"name":"alice","age":38}""".encodeToByteArray()

        // when
        val result = personSerializer.deserialize(source)

        // then
        assertThat(result).isDataClassEqualTo(SerializablePerson("alice", 38))
    }
}
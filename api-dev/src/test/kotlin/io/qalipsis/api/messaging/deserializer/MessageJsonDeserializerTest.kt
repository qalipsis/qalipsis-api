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

package io.qalipsis.api.messaging.deserializer

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Test
import java.time.Instant

internal class MessageJsonDeserializerTest {

    internal data class UserExample(val id: Int, val name: String, val createdAt: Instant)

    companion object {
        private val mapper =
            JsonMapper().registerModule(JavaTimeModule()).registerModule(KotlinModule.Builder().build())
    }

    @Test
    fun `should deserialize byte array to string`() {
        val deserializer = MessageJsonDeserializer(UserExample::class)

        val user = UserExample(1, "bob", Instant.now())
        val jsonObject = mapper.writeValueAsBytes(user)

        val result = deserializer.deserialize(jsonObject)

        assertThat(result).all {
            prop(UserExample::name).isEqualTo("bob")
            prop(UserExample::id).isEqualTo(1)
            prop(UserExample::createdAt).isEqualTo(user.createdAt)
        }
    }

    @Test
    fun `should fail when deserializing byte array to string with the default object mapper`() {
        val deserializer = MessageJsonDeserializer(UserExample::class)

        val instantString = "2021-03-04T12:17:47.720Z"
        val userString = """{"id":1, "name":"bob", "createdAt":"$instantString", "email":"bob@mail.com"}"""
            .trimMargin()

        org.junit.jupiter.api.assertThrows<JsonMappingException> {
            deserializer.deserialize(userString.toByteArray())
        }
    }

    @Test
    fun `should deserialize byte array to string with custom mapper configuration`() {
        val deserializer = MessageJsonDeserializer(UserExample::class) {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        val instantString = "2021-03-04T12:17:47.720Z"
        val userString = """{"id":2, "name":"bob", "createdAt":"$instantString", "email":"bob@mail.com"}"""
            .trimMargin()

        val result = deserializer.deserialize(userString.toByteArray())

        assertThat(result).all {
            prop(UserExample::name).isEqualTo("bob")
            prop(UserExample::id).isEqualTo(2)
            prop(UserExample::createdAt).isEqualTo(Instant.parse(instantString))
        }
    }
}
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

package io.qalipsis.api.events

import java.time.Instant
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Logger for events in order to track what exactly happens in Qalipsis.
 *
 * The [EventsLogger] is coupled with [EventsPublisher]s, that are in charge of publishing the events to remote systems
 * or log files.
 *
 * @author Eric Jessé
 */
interface EventsLogger {

    /**
     * Adds [tags] to all the generated events.
     */
    fun configureTags(tags: Map<String, String>)

    /**
     * Log an event with the provided level.
     *
     * @param level the priority of the event.
     * @param name the name of the event. They are dot-separated strings of kebab-cased (dash-separated lowercase words) token
     * and in the form of "object-state", e.g: step-started, minion.execution-complete.
     * @param value the potential value. Any type can be used, its interpretation is let to the implementation.
     * @param timestamp the instant of the event, defaults to now.
     * @param tagsSupplier statement to generate the map of tags, if the event ever has to be logged.
     */
    fun log(
        level: EventLevel, name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tagsSupplier: () -> Map<String, String>
    )

    /**
     * Log an event with the provided level. If creating the map of tags has a cost, prefer to the use
     * the equivalent method with a tags supplier expression.
     *
     * @param level the priority of the event.
     * @param name the name of the event. They are dot-separated strings of kebab-cased (dash-separated lowercase words) token
     * and in the form of "object-state", e.g: step-started, minion.execution-complete.
     * @param value the potential value. Any type can be used, its interpretation is let to the implementation.
     * @param timestamp the instant of the event, defaults to now.
     * @param tags a map of tags.
     */
    fun log(
        level: EventLevel, name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tags: Map<String, String> = emptyMap()
    )

    fun log(
        level: EventLevel, name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        vararg tags: Pair<String, String>
    ) {
        log(level, name, value, timestamp) { tags.toMap() }
    }

    fun trace(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tags: Map<String, String> = emptyMap()
    ) {
        log(EventLevel.TRACE, name, value, timestamp, tags)
    }

    fun trace(name: String, value: Any? = null, timestamp: Instant = Instant.now(), vararg tags: Pair<String, String>) {
        log(EventLevel.TRACE, name, value, timestamp, *tags)
    }

    fun trace(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tagsSupplier: (() -> Map<String, String>)
    ) {
        log(EventLevel.TRACE, name, value, timestamp, tagsSupplier)
    }

    fun debug(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tags: Map<String, String> = emptyMap()
    ) {
        log(EventLevel.DEBUG, name, value, timestamp, tags)
    }

    fun debug(name: String, value: Any? = null, timestamp: Instant = Instant.now(), vararg tags: Pair<String, String>) {
        log(EventLevel.DEBUG, name, value, timestamp, *tags)
    }

    fun debug(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tagsSupplier: (() -> Map<String, String>)
    ) {
        log(EventLevel.DEBUG, name, value, timestamp, tagsSupplier)
    }

    fun info(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tags: Map<String, String> = emptyMap()
    ) {
        log(EventLevel.INFO, name, value, timestamp, tags)
    }

    fun info(name: String, value: Any? = null, timestamp: Instant = Instant.now(), vararg tags: Pair<String, String>) {
        log(EventLevel.INFO, name, value, timestamp, *tags)
    }

    fun info(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tagsSupplier: (() -> Map<String, String>)
    ) {
        log(EventLevel.INFO, name, value, timestamp, tagsSupplier)
    }

    fun warn(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tags: Map<String, String> = emptyMap()
    ) {
        log(EventLevel.WARN, name, value, timestamp, tags)
    }

    fun warn(name: String, value: Any? = null, timestamp: Instant = Instant.now(), vararg tags: Pair<String, String>) {
        log(EventLevel.WARN, name, value, timestamp, *tags)
    }

    fun warn(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tagsSupplier: (() -> Map<String, String>)
    ) {
        log(EventLevel.WARN, name, value, timestamp, tagsSupplier)
    }

    fun error(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tags: Map<String, String> = emptyMap()
    ) {
        log(EventLevel.ERROR, name, value, timestamp, tags)
    }

    fun error(name: String, value: Any? = null, timestamp: Instant = Instant.now(), vararg tags: Pair<String, String>) {
        log(EventLevel.ERROR, name, value, timestamp, *tags)
    }

    fun error(
        name: String, value: Any? = null, timestamp: Instant = Instant.now(),
        tagsSupplier: (() -> Map<String, String>)
    ) {
        log(EventLevel.ERROR, name, value, timestamp, tagsSupplier)
    }

    /**
     * Starts the logger.
     */
    @PostConstruct
    fun start() = Unit

    /**
     * Closes the logger.
     */
    @PreDestroy
    fun stop() = Unit
}

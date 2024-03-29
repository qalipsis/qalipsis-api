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

package io.qalipsis.api.messaging

import io.qalipsis.api.messaging.subscriptions.SlotBasedSubscription
import io.qalipsis.api.messaging.subscriptions.TopicSubscription
import io.qalipsis.api.sync.ImmutableSlot
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.time.Duration

/**
 * [AbstractLinkedSlotsBasedTopic] is a special implementation of [Topic] providing the data as an infinite loop: once the consumer polled
 * all the values from the topic, values are provided from the beginning.
 *
 * @property idleTimeout idle time of a subscription: once a subscription passed this duration without record, it is cancelled.
 *
 * @author Eric Jessé
 */
internal abstract class AbstractLinkedSlotsBasedTopic<T>(
    protected val idleTimeout: Duration
) : Topic<T> {

    protected var open = true

    /**
     * Slot for the next subscriptions.
     */
    protected var subscriptionSlot = ImmutableSlot<LinkedRecord<T>>()

    /**
     * Slot for the position of writing in the topic.
     */
    protected var writeSlot = subscriptionSlot

    protected val subscriptionMutex = Mutex(false)

    protected val writeMutex = Mutex(false)

    protected val subscriptions = mutableMapOf<String, SlotBasedSubscription<T>>()

    override suspend fun subscribe(subscriberId: String): TopicSubscription<T> {
        verifyState()
        return if (subscriptions.containsKey(subscriberId)) {
            log.trace { "Returning existing subscription for $subscriberId" }
            subscriptions[subscriberId]!!
        } else {
            subscriptionMutex.withLock {
                if (subscriptions.containsKey(subscriberId)) {
                    log.trace { "Returning existing subscription for $subscriberId" }
                    subscriptions[subscriberId]!!
                } else {
                    log.trace { "Creating new subscription for $subscriberId" }
                    SlotBasedSubscription.create(subscriberId, subscriptionSlot, idleTimeout) {
                        subscriptions.remove(subscriberId)
                    }.also {
                        subscriptions[subscriberId] = it
                        log.trace { "New subscription created for $subscriberId" }
                    }
                }
            }
        }
    }

    override suspend fun produceValue(value: T) {
        verifyState()
        produce(Record(value = value))
    }

    override suspend fun produce(record: Record<T>) {
        log.trace { "Adding the record $record to the topic" }
        val linkedRecord = LinkedRecordWithValue(record)
        writeMutex.withLock {
            writeSlot.set(linkedRecord)
            updateSubscriptionSlot(writeSlot)
            writeSlot = linkedRecord.next
        }
        log.trace { "The record $record was added to the topic" }
    }

    /**
     * Updates the position of the slot for the future new subscribers.
     */
    abstract suspend fun updateSubscriptionSlot(lastSetSlot: ImmutableSlot<LinkedRecord<T>>)

    override suspend fun poll(subscriberId: String): Record<T> {
        log.trace { "Polling next record for subscription $subscriberId" }
        verifyState()
        return subscriptions[subscriberId]?.poll() ?: error("The subscription $subscriberId no longer exists")
    }

    override suspend fun pollValue(subscriberId: String): T {
        log.trace { "Polling next value for subscription $subscriberId" }
        return poll(subscriberId).value
    }

    override fun cancel(subscriberId: String) {
        log.trace { "Cancelling subscription $subscriberId" }
        subscriptions.remove(subscriberId)?.let {
            it.cancel()
            log.trace { "Subscription $subscriberId was found and cancelled" }
        }
    }

    override fun close() {
        log.trace { "Closing topic" }
        open = false
        subscriptions.values.forEach { it.cancel() }
        subscriptions.clear()
    }

    override suspend fun complete() {
        // Nothing to do.
    }

    private fun verifyState() {
        if (!open) {
            throw ClosedTopicException()
        }
    }

    companion object {
        @JvmStatic
        private val log = KotlinLogging.logger { }
    }

}

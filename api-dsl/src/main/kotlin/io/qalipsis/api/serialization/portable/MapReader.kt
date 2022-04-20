package io.qalipsis.api.serialization.portable

import io.micronaut.core.util.clhm.ConcurrentLinkedHashMap
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap

interface MapReader {

    fun <K, V> readKotlinMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): MutableMap<K, V>?

    fun <K, V> readJavaMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): java.util.Map<K, V>?

    fun <K, V> readHashMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): HashMap<K, V>?

    fun <K, V> readLinkedHashMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): LinkedHashMap<K, V>?

    fun <K, V> readConcurrentMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): ConcurrentMap<K, V>?

    fun <K, V> readConcurrentHashMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): ConcurrentHashMap<K, V>?

    fun <K, V> readConcurrentLinkedHashMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): ConcurrentLinkedHashMap<K, V>?

    fun <K, V> readConcurrentSkipListMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): ConcurrentSkipListMap<K, V>?

    fun <K, V> readTreeMap(
        nullable: Boolean = false,
        keyReader: (Reader) -> K,
        valueReader: (Reader) -> V
    ): TreeMap<K, V>?
}
package io.qalipsis.api.serialization.portable

import io.micronaut.core.util.clhm.ConcurrentLinkedHashMap
import java.util.TreeMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap

interface MapWriter {

    fun <K, V> writeKotlinMap(
        map: Map<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeJavaMap(
        map: java.util.Map<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeHashMap(
        map: HashMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeLinkedHashMap(
        map: LinkedHashMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeConcurrentMap(
        map: ConcurrentMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeConcurrentHashMap(
        map: ConcurrentHashMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeConcurrentLinkedHashMap(
        map: ConcurrentLinkedHashMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeConcurrentSkipListMap(
        map: ConcurrentSkipListMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

    fun <K, V> writeTreeMap(
        map: TreeMap<K, V>?,
        nullable: Boolean = false,
        keyWriter: (Writer, K?) -> Unit,
        valueWriter: (Writer, V?) -> Unit
    )

}
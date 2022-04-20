package io.qalipsis.api.serialization.portable

import java.util.Optional
import kotlin.reflect.KClass

interface Writer {

    fun writeSize(size: Int)

    fun <T : Enum<*>> writeEnum(enum: T?, nullable: Boolean = false)

    fun <T : Any> writeObject(value: T?, declaredType: KClass<T>, nullable: Boolean = false)

    fun <T, O : Optional<T>> writeOptional(optional: O?, nullable: Boolean = false, valueWriter: (Writer, T?) -> Unit)

    val javaTypes: JavaTypesWriter

    val kotlinTypes: KotlinTypesWriter

    val collections: CollectionWriter

    val maps: MapWriter

}
package io.qalipsis.api.serialization.portable

import java.util.Optional
import kotlin.reflect.KClass

interface Reader {

    fun readSize(): Int

    fun <T : Enum<*>> readEnum(nullable: Boolean = false): T?

    fun <T : Any> readObject(declaredType: KClass<T>, nullable: Boolean = false): T?

    fun <T, O : Optional<T>> readOptional(nullable: Boolean = false, valueReader: (Reader) -> T): O?

    val javaTypes: JavaTypesReader

    val kotlinTypes: KotlinTypesReader

    val collections: CollectionReader

    val maps: MapReader

}
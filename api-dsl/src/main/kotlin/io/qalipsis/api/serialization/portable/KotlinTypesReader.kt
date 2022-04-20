package io.qalipsis.api.serialization.portable

import kotlin.reflect.KClass

interface KotlinTypesReader {

    fun readBoolean(nullable: Boolean = false): Boolean?

    fun readChar(nullable: Boolean = false): Char?

    fun readByte(nullable: Boolean = false): Byte?

    fun readShort(nullable: Boolean = false): Short?

    fun readInt(nullable: Boolean = false): Int?

    fun readLong(nullable: Boolean = false): Long?

    fun readFloat(nullable: Boolean = false): Float?

    fun readDouble(nullable: Boolean = false): Double?

    fun readString(nullable: Boolean = false): String?

    fun readKClass(nullable: Boolean = false): KClass<*>?


}
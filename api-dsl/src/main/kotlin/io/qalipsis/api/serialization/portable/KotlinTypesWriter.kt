package io.qalipsis.api.serialization.portable

import kotlin.reflect.KClass

interface KotlinTypesWriter {


    fun writeBoolean(boolean: Boolean?, nullable: Boolean = false)

    fun writeChar(char: Char?, nullable: Boolean = false)

    fun writeByte(byte: Byte?, nullable: Boolean = false)

    fun writeShort(short: Short?, nullable: Boolean = false)

    fun writeInt(int: Int?, nullable: Boolean = false)

    fun writeLong(long: Long?, nullable: Boolean = false)

    fun writeFloat(float: Float?, nullable: Boolean = false)

    fun writeDouble(double: Double?, nullable: Boolean = false)

    fun writeString(string: String?, nullable: Boolean = false)

    fun writeKClass(value: KClass<*>?, nullable: Boolean = false)

}
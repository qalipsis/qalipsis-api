package io.qalipsis.api.serialization.portable

import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date
import java.util.UUID

interface JavaTypesWriter {

    fun writeBoolean(boolean: java.lang.Boolean?, nullable: Boolean = false)

    fun writeChar(char: java.lang.Character?, nullable: Boolean = false)

    fun writeByte(byte: java.lang.Byte?, nullable: Boolean = false)

    fun writeShort(short: java.lang.Short?, nullable: Boolean = false)

    fun writeInt(int: java.lang.Integer?, nullable: Boolean = false)

    fun writeLong(long: java.lang.Long?, nullable: Boolean = false)

    fun writeFloat(float: java.lang.Float?, nullable: Boolean = false)

    fun writeDouble(double: java.lang.Double?, nullable: Boolean = false)

    fun writeDate(date: Date?, nullable: Boolean = false)

    fun writeBigInteger(bigInteger: BigInteger?, nullable: Boolean = false)

    fun writeBigDecimal(bigDecimal: BigDecimal?, nullable: Boolean = false)

    fun writeString(string: java.lang.String?, nullable: Boolean = false)

    fun writeUUID(string: UUID?, nullable: Boolean = false)

    fun writeClass(value: Class<*>?, nullable: Boolean = false)

}
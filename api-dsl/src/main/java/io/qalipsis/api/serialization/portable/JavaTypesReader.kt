package io.qalipsis.api.serialization.portable

import java.lang.String
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date
import java.util.UUID

interface JavaTypesReader {

    fun readBoolean(nullable: Boolean = false): java.lang.Boolean?

    fun readChar(nullable: Boolean = false): java.lang.Character?

    fun readByte(nullable: Boolean = false): java.lang.Byte?

    fun readShort(nullable: Boolean = false): java.lang.Short?

    fun readInt(nullable: Boolean = false): java.lang.Integer?

    fun readLong(nullable: Boolean = false): java.lang.Long?

    fun readFloat(nullable: Boolean = false): java.lang.Float?

    fun readDouble(nullable: Boolean = false): java.lang.Double?

    fun readDate(nullable: Boolean = false): Date?

    fun readBigInteger(nullable: Boolean = false): BigInteger?

    fun readBigDecimal(nullable: Boolean = false): BigDecimal?

    fun readString(nullable: Boolean = false): String?

    fun readUUID(nullable: Boolean = false): UUID?

    fun readClass(nullable: Boolean = false): Class<*>?

}
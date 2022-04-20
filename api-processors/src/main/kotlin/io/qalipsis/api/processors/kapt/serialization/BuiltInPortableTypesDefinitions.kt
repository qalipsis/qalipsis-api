package io.qalipsis.api.processors.kapt.serialization

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.micronaut.core.util.clhm.ConcurrentLinkedHashMap
import io.qalipsis.api.processors.kapt.TypeUtils
import mu.KotlinLogging
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Date
import java.util.TreeMap
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentSkipListMap
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

@KotlinPoetMetadataPreview
internal class BuiltInPortableTypesDefinitions(
    private val elements: Elements,
    private val types: Types,
    private val typeUtils: TypeUtils
) {

    fun getStatements(property: SerializableProperty): Statements {
        return getStatements("value.${property.name}", property.type)
    }

    private fun getStatements(
        writablePath: String,
        type: PortableType
    ): Statements {
        val nullable = type.isNullable
        val nullableMarker = if (nullable) "" else "!!"
        val propertyType = type.name
        return when {
            propertyType in BUILT_IN_TYPES.keys -> BUILT_IN_TYPES[propertyType]!!.let { statements ->
                Statements(
                    "${statements.reader}($nullable)$nullableMarker",
                    "${statements.writer}(${writablePath}, $nullable)"
                )
            }
            typeUtils.isCollection(type.type) -> forCollection(type, nullable, nullableMarker, writablePath)
            typeUtils.isMap(type.type) -> forMap(type, nullable, nullableMarker, writablePath)
            else -> forObject(type, nullable, nullableMarker, writablePath)
        }
    }

    private fun forCollection(
        type: PortableType,
        nullable: Boolean,
        nullableMarker: String,
        writablePath: String
    ): Statements {
        val valueType = getDeclaredType(type.parameters[0])
        val valueStatements = getStatements("item", type.parameters[0])
        return Statements(
            "collections.readCollection<${valueType}, ${type.name}>($nullable, { valueReader -> valueReader.${valueStatements.reader} })$nullableMarker",
            "collections.writeCollection(${writablePath}, $nullable, { valueWriter, item -> valueWriter.${valueStatements.writer} })"
        )
    }

    private fun forMap(
        type: PortableType,
        nullable: Boolean,
        nullableMarker: String,
        writablePath: String
    ): Statements {
        val keyType = getDeclaredType(type.parameters[0])
        val valueType = getDeclaredType(type.parameters[1])

        val keyStatements = getStatements("key", type.parameters[0])
        val valueStatements = getStatements("value", type.parameters[1])
        val mapStatements = BUILT_IN_MAPS[(type.name as ParameterizedTypeName).rawType]!!

        return Statements(
            "${mapStatements.reader}<${keyType}, ${valueType}>($nullable, { keyReader -> keyReader.${keyStatements.reader} }, { valueReader -> valueReader.${valueStatements.reader} })$nullableMarker as ${type.name}",
            "${mapStatements.writer}(${writablePath}, $nullable, { keyWriter, key -> keyWriter.${keyStatements.writer} }, { valueWriter, value -> valueWriter.${valueStatements.writer} })"
        )
    }

    private fun forObject(
        type: PortableType,
        nullable: Boolean,
        nullableMarker: String,
        writablePath: String
    ): Statements {
        val declaredType = getDeclaredType(type)
        return if (type.name == STAR) {
            Statements(
                "readObject<Any>(Any::class, $nullable)$nullableMarker as $declaredType",
                "writeObject<Any>(${writablePath} as $declaredType, Any::class, $nullable)"
            )
        } else {
            val castingType = type.name
            val declaredClass =
                (type.name as? ParameterizedTypeName)?.rawType ?: ((type.name as? ClassName)?.canonicalName)
                ?: declaredType
            val nonNullDeclaredType = getDeclaredType(type, false)
            Statements(
                "readObject<$nonNullDeclaredType>(${declaredClass}::class, $nullable)$nullableMarker as $castingType",
                "writeObject<$nonNullDeclaredType>(${writablePath} as $castingType, ${declaredClass}::class, $nullable)"
            )
        }
    }

    private fun getDeclaredType(portableType: PortableType, nullable: Boolean = portableType.isNullable): String {
        return if (portableType.name == STAR) {
            "Any${if (nullable) "?" else ""}"
        } else {
            portableType.name.copy(nullable = nullable).toString()
        }
    }

    data class Statements(val reader: String, val writer: String)

    private companion object {
        /**
         *
         *
        byte[], boolean[], char[], short[], int[], long[], float[], double[], String[]
        ArrayList, LinkedList, CopyOnWriteArrayList/Set, HashSet, ConcurrentSkipListSet, LinkedHashSet,
        TreeSet, ArrayDeque, LinkedBlockingQueue, ArrayBlockingQueue, PriorityBlockingQueue, PriorityQueue, DelayQueue, SynchronousQueue, LinkedTransferQueue

         */

        private val BUILT_IN_TYPES = listOf<KClass<*>>(
            Boolean::class,
            Char::class,
            Byte::class,
            Short::class,
            Int::class,
            Long::class,
            Float::class,
            Double::class,
            BooleanArray::class,
            CharArray::class,
            ByteArray::class,
            ShortArray::class,
            IntArray::class,
            LongArray::class,
            FloatArray::class,
            DoubleArray::class,
            String::class,
            UUID::class,
            KClass::class,
            Date::class,
            BigInteger::class,
            BigDecimal::class
        ).flatMap { expandToTypeNamesAndStatementsForSimpleTypes(it) }.toMap()

        private val BUILT_IN_MAPS = listOf<KClass<*>>(
            Map::class,
            MutableMap::class,
            HashMap::class,
            LinkedHashMap::class,
            ConcurrentMap::class,
            ConcurrentHashMap::class,
            ConcurrentLinkedHashMap::class,
            ConcurrentSkipListMap::class,
            TreeMap::class,
        ).flatMap { expandToTypeNamesAndStatementsForMaps(it) }.toMap()

        private fun expandToTypeNamesAndStatementsForSimpleTypes(type: KClass<*>): Collection<Pair<TypeName, Statements>> {
            return mutableListOf(
                type.asTypeName() to Statements(
                    "kotlinTypes.read${type.simpleName}",
                    "kotlinTypes.write${type.simpleName}"
                ),
                type.javaObjectType.asTypeName() to Statements(
                    "javaTypes.read${type.simpleName}",
                    "javaTypes.write${type.simpleName}"
                )
            )
        }

        private fun expandToTypeNamesAndStatementsForMaps(type: KClass<*>): Collection<Pair<TypeName, Statements>> {
            val kotlinType = type.asTypeName()
            val javaType = type.javaObjectType.asTypeName()
            return if (kotlinType == javaType) {
                mutableListOf(
                    type.asTypeName() to Statements(
                        "maps.read${type.simpleName}",
                        "maps.write${type.simpleName}"
                    )
                )
            } else {
                mutableListOf(
                    type.asTypeName() to Statements(
                        "maps.readKotlin${type.simpleName}",
                        "maps.writeKotlin${type.simpleName}"
                    ),
                    type.javaObjectType.asTypeName() to Statements(
                        "maps.readJava${type.simpleName}",
                        "maps.writeJava${type.simpleName}"
                    )
                )
            }
        }

        private val log = KotlinLogging.logger { }

        init {
            log.trace { "Built-in portable types: ${BUILT_IN_TYPES.keys + BUILT_IN_MAPS.keys}" }
        }

    }

}
package io.qalipsis.api.processors.serializable

import io.qalipsis.api.serialization.Portable
import java.util.UUID

/**
 * It should not be serialized, because abstract.
 */
@Portable
internal interface Wrapper<U, I : Collection<U?>> {

    val values: I

}

/**
 * It should not be serialized, because abstract.
 */
@Portable
internal abstract class AbstractWrapper<U, I : Collection<U?>> : Wrapper<U, I> {

    var uuid = UUID.randomUUID()

}

/**
 * It should not be serialized, because abstract.
 */
@Portable
internal class ConcreteWrapper<U : Number, I : Collection<U?>> : AbstractWrapper<U, I>() {

    override lateinit var values: I

    var keyedValues: Map<U?, I> = emptyMap()

    lateinit var keyedAny: Map<U, *>

}


@Portable
internal data class DataWrapper(
    val name: String,
    override val values: List<Int>
) : AbstractWrapper<Int, List<Int>>() {

    /**
     * There is no need to serialize this field, since it can be recreated from the constructor arguments.
     */
    val readonlyName = name.lowercase()

}
/*
@Portable
internal open class OpenWrapper<U : AbstractWrapper<*, *>>(
    values: Collection<U>
) : AbstractWrapper<U, Set<U>>() {

    override val values: Set<U> = values.toSet()
}


@Portable
internal class ClosedWrapper(
    values: Collection<DataWrapper>
) : OpenWrapper<DataWrapper>(values) {

    lateinit var type: Type
}

@Portable
internal enum class Type {
    ANY, OTHER
}

@Portable
internal class ClosedAndReadonly(firstName: String, lastName: String) {

    /**
     * This field has to be serialized, since it is not safely reversible into the constructor arguments.
     */
    val name: String = "$firstName $lastName"

}

 */
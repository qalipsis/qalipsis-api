package io.qalipsis.api.serialization.portable

interface CollectionWriter {

    fun <T, I : Collection<T>> writeCollection(
        iterable: I?,
        nullable: Boolean = false,
        valueWriter: (Writer, T?) -> Unit
    )

}
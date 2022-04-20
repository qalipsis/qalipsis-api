package io.qalipsis.api.serialization.portable

interface CollectionReader {

    fun <T, I : Collection<T>> readCollection(nullable: Boolean = false, valueReader: (Reader) -> T): I?

}
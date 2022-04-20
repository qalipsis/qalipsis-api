package io.qalipsis.api.serialization

/**
 * When the class is annotated with [Portable], specifies a function to use to read data to serialize.
 * There should be a monadic function annotated with [PortableWriter], accepting a type compliant with this return.
 *
 * Example:
 * ```kotlin
 * class Closed(@PortableDefault("") firstName : String, PortableDefault("") lastName: String) {
 *
 *    @get:PortableReader("name")
 *    val name : String = "$firstName $lastName"
 *
 *    @PortableWriter("name")
 *    internal fun writeName(name: String) {
 *      this.name = name
 *    }
 *
 * }
 * ```
 *
 * @author Eric Jessé
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.FUNCTION)
annotation class PortableReader(

    /**
     * Name of the serialized property, should match the one of the related [PortableWriter].
     */
    val value: String
)

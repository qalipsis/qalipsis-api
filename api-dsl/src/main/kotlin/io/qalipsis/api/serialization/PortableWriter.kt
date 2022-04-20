package io.qalipsis.api.serialization

/**
 * Specifies the point of writing for a serializable value.
 *
 * When the class is annotated with [Portable], specifies an monadic function to use to write data to serialize.
 *
 * There should be a niladic function annotated with [PortableReader], returning a type that can be provided as argument.
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
 *
 * @author Eric Jessé
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
annotation class PortableWriter(

    /**
     * Name of the serialized property, should match the one of the related [PortableWriter].
     *
     * Defaults to the name of the annotated argument, requires when on a function.
     */
    val value: String = ""
)

package io.qalipsis.api.serialization

/**
 * When the class is annotated with [Portable], specifies the default value to use on a nullable constructor argument
 * to allow the creation of a new instance
 *
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
annotation class PortableDefault(

    val value: String = ""
)

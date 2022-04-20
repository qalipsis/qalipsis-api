package io.qalipsis.api.serialization

import javax.validation.constraints.NotEmpty
import kotlin.reflect.KClass

/**
 * TODO
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FILE)
annotation class Portable(

    @get:NotEmpty
    val types: Array<KClass<*>> = []
)

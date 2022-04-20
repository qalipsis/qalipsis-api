package io.qalipsis.api.processors.kapt

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import io.qalipsis.api.serialization.Serializable
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException

@KotlinPoetMetadataPreview
internal class AnnotationUtils(
    private val typeUtils: TypeUtils
) {

    /**
     * Extract the list of types specified in the annotation [Serializable].
     */
    fun getSupportedTypes(typeExtractor: () -> Any?): Collection<TypeElement> {
        // Here, it is a bit tricky. It is not possible to get the classes, but the generated exception
        // "javax.lang.model.type.MirroredTypesException: Attempt to access Class objects for TypeMirrors" actually
        // contains the [TypeMirror]s we need. Long story here:
        // https://area-51.blog/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
        return try {
            typeExtractor()
            null
        } catch (e: MirroredTypesException) {
            e.typeMirrors.mapNotNull(typeUtils::getTypeElement)
        } catch (e: Exception) {
            null
        }?.takeIf(Collection<*>::isNotEmpty) ?: emptyList()
    }

}
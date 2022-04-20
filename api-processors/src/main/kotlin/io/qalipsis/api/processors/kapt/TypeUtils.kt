package io.qalipsis.api.processors.kapt

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isObject
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import java.util.Optional
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.WildcardType
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Utility class to find out the type of a type.
 *
 * @author Eric Jessé
 */
@KotlinPoetMetadataPreview
internal class TypeUtils(elementUtils: Elements, private val typeUtils: Types) {

    private val mapType = typeUtils.erasure(elementUtils.getTypeElement(Map::class.java.name).asType())

    private val iterableType = typeUtils.erasure(elementUtils.getTypeElement(Iterable::class.java.name).asType())

    private val collectionType = typeUtils.erasure(elementUtils.getTypeElement(Collection::class.java.name).asType())

    private val optionalType = typeUtils.erasure(elementUtils.getTypeElement(Optional::class.java.name).asType())

    /**
     * Verifies if the element passed as parameter is a Kotlin Object.
     */
    fun isAKotlinObject(typeElement: TypeElement) = typeElement.toImmutableKmClass().isObject

    fun isIterable(type: TypeMirror): Boolean {
        return if (type is DeclaredType && type.typeArguments.size == 1) {
            return typeUtils.isSubtype(typeUtils.erasure(type), iterableType)
        } else {
            false
        }
    }

    fun isCollection(type: TypeMirror): Boolean {
        return if (type is DeclaredType && type.typeArguments.size == 1) {
            return typeUtils.isSubtype(typeUtils.erasure(type), iterableType)
        } else {
            false
        }
    }

    fun isOptional(type: TypeMirror): Boolean {
        return if (type is DeclaredType && type.typeArguments.size == 1) {
            return typeUtils.isSubtype(typeUtils.erasure(type), optionalType)
        } else {
            false
        }
    }

    fun isMap(type: TypeMirror): Boolean {
        return if (type is DeclaredType && type.typeArguments.size == 2) {
            return typeUtils.isSubtype(typeUtils.erasure(type), mapType)
        } else {
            false
        }
    }

    fun getTypeOfFirstGeneric(type: DeclaredType): TypeMirror {
        var genericType = type.typeArguments.first()
        if (genericType is WildcardType) {
            genericType = typeUtils.erasure(genericType)
        }
        return genericType
    }

    fun erase(type: TypeMirror): TypeMirror {
        return typeUtils.erasure(type)
    }

    /**
     * Returns the [TypeElement] for the [TypeMirror] passed as parameter if it exists.
     * Primitive types returns null.
     */
    fun getTypeElement(typeMirror: TypeMirror) = typeUtils.asElement(typeMirror) as TypeElement?
}

/*
 * Copyright 2022 AERIS IT Solutions GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package io.qalipsis.api.processors

import java.util.Optional
import javax.lang.model.element.ElementKind
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
internal class TypeUtils(private val elementUtils: Elements, private val typeUtils: Types) {

    private val iterableType = typeUtils.erasure(elementUtils.getTypeElement(Iterable::class.java.name).asType())

    private val optionalType = typeUtils.erasure(elementUtils.getTypeElement(Optional::class.java.name).asType())

    /**
     * Verifies if the element passed as parameter is a Kotlin Object.
     */
    fun isAKotlinObject(typeElement: TypeElement) =
        elementUtils.getAllMembers(typeElement)
            .any { it.kind == ElementKind.FIELD && it.simpleName.toString() == "INSTANCE" }


    fun isIterableWithGeneric(type: TypeMirror): Boolean {
        return if (type is DeclaredType && type.typeArguments.size == 1) {
            return typeUtils.isSubtype(typeUtils.erasure(type), iterableType)
        } else {
            false
        }
    }

    fun isOptionalWithGeneric(type: TypeMirror): Boolean {
        return if (type is DeclaredType && type.typeArguments.size == 1) {
            return typeUtils.isSubtype(typeUtils.erasure(type), optionalType)
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

package io.qalipsis.api.processors.kapt.serialization

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.ImmutableKmTypeParameter
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isNullable
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmVariance
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

@KotlinPoetMetadataPreview
internal class PortableTypeHelper {

    fun expandJavaTypeParameters(typeParameters: List<TypeParameterElement>): Map<String, PortableType> {
        val typeParametersByName = mutableMapOf<String, PortableType>()
        typeParameters.forEach { typeParam ->
            val name = "${typeParam.simpleName}"
            typeParametersByName[name] = expandTypeNames(name, typeParam.bounds.first(), true, typeParametersByName)
        }
        return typeParametersByName
    }

    fun extractJavaProperties(
        serializableType: TypeElement,
        typeParameters: Map<String, PortableType>
    ): Map<String, SerializableProperty> {
        val propertiesByName = mutableMapOf<String, SerializableProperty>()
        propertiesByName += serializableType.enclosedElements.asSequence()
            .filter { it.kind == ElementKind.METHOD }
            .map { it as ExecutableElement }
            .filter { it.parameters.isEmpty() && Modifier.STATIC !in it.modifiers }
            .filter { it.simpleName.matches(Regex("get[A-Z].*")) }
            .associate { getter ->
                val name = "${getter.simpleName}".substringAfter("get").replaceFirstChar { it.lowercase() }
                val propertyType = getter.returnType
                val type = typeParameters["$propertyType"] ?: expandTypeNames(
                    "${propertyType.asTypeName()}",
                    propertyType,
                    true,
                    typeParameters
                )
                val hasSetter = hasSetter(serializableType, name)
                val mutable = serializableType.enclosedElements.asSequence()
                    .filter { it.kind.isField }
                    .map { it as VariableElement }
                    .any { Modifier.PUBLIC in it.modifiers && Modifier.STATIC !in it.modifiers }
                name to SerializableProperty(name, type, mutable, hasSetter)
            }

        propertiesByName += serializableType.enclosedElements.asSequence()
            .filter { it.kind.isField }
            .map { it as VariableElement }
            .filter { Modifier.PUBLIC in it.modifiers && Modifier.STATIC !in it.modifiers }
            .associate { field ->
                val name = "${field.simpleName}"
                val propertyType = field.asType()
                val type = typeParameters["$propertyType"] ?: expandTypeNames(
                    "${propertyType.asTypeName()}",
                    propertyType,
                    true,
                    typeParameters
                )
                val hasSetter = hasSetter(serializableType, name)
                name to SerializableProperty(name, type, true, hasSetter)
            }
        return propertiesByName
    }

    private fun hasSetter(serializableType: TypeElement, name: String) =
        serializableType.enclosedElements.asSequence()
            .filter { it.kind == ElementKind.METHOD }
            .map { it as ExecutableElement }
            .filter { it.parameters.isEmpty() && Modifier.STATIC !in it.modifiers }
            .any { "${it.simpleName}" == "set${name.replaceFirstChar { it.uppercase() }}" }

    fun expandKotlinTypeParameters(
        kotlinTypeParameters: List<ImmutableKmTypeParameter>,
        typeParameters: MutableMap<String, PortableType>
    ) {
        if (kotlinTypeParameters.isNotEmpty() && typeParameters.isNotEmpty()) {
            kotlinTypeParameters.forEach { typeParam ->
                reconfigureTypeParametersFromKotlin(typeParam.name, typeParam.upperBounds.first(), typeParameters)
                // Also access to the type by its ID.
                typeParameters["${typeParam.id}"] = typeParameters[typeParam.name]!!
            }
        }
    }

    fun createTypeName(
        propertyType: ImmutableKmType,
        existingPortableType: PortableType,
        typeParameters: MutableMap<String, PortableType>
    ): PortableType {
        val parameters = propertyType.arguments.mapIndexed { index, argumentType ->
            when (argumentType.variance) {
                KmVariance.INVARIANT -> createTypeName(
                    argumentType.type!!,
                    existingPortableType.parameters[index],
                    typeParameters
                )
                KmVariance.IN -> {
                    existingPortableType.parameters[index].let {
                        it.copy(
                            name = WildcardTypeName.consumerOf(
                                createTypeName(argumentType.type!!, it, typeParameters).name
                            )
                        )
                    }
                }
                KmVariance.OUT -> {
                    existingPortableType.parameters[index].let {
                        it.copy(
                            name = WildcardTypeName.producerOf(
                                createTypeName(argumentType.type!!, it, typeParameters).name
                            )
                        )
                    }
                }
                else -> existingPortableType.parameters[index].copy(name = STAR)
            }
        }

        val classifier = propertyType.classifier
        return if (classifier is KmClassifier.TypeParameter) {
            val typeName = typeParameters["${classifier.id}"]!!
            typeName.copy(
                isNullable = propertyType.isNullable,
                name = typeName.name.copy(nullable = propertyType.isNullable)
            )
        } else {
            val typeName =
                getKotlinTypeName(classifier, parameters.map { it.name }).copy(nullable = propertyType.isNullable)
            existingPortableType.copy(name = typeName, parameters = parameters, isNullable = propertyType.isNullable)
        }
    }

    private fun reconfigureTypeParametersFromKotlin(
        name: String,
        type: ImmutableKmType,
        typeParameters: MutableMap<String, PortableType>
    ) {
        val portableType = typeParameters[name]!!
        val parameters = portableType.parameters.mapIndexed { index, parameterPortableType ->
            (typeParameters[parameterPortableType.declaredName] ?: parameterPortableType)
                .copy(isNullable = type.arguments[index].type?.isNullable ?: true)
        }
        val classifier = type.classifier
        val typeName = getKotlinTypeName(classifier, parameters.map { it.name })

        typeParameters[name] = portableType.copy(name = typeName, isNullable = type.isNullable, parameters = parameters)
    }

    private fun getKotlinTypeName(
        classifier: KmClassifier,
        parameterTypeNames: List<TypeName>
    ): TypeName {
        val classifierName = when (classifier) {
            is KmClassifier.Class -> classifier.name
            is KmClassifier.TypeAlias -> classifier.name
            else -> throw IllegalArgumentException("Classifier $classifier is not supported")
        }.replace('/', '.')

        val typeName = if (parameterTypeNames.isEmpty()) {
            ClassName.bestGuess(classifierName)
        } else {
            ClassName.bestGuess(classifierName).parameterizedBy(parameterTypeNames)
        }
        return typeName
    }

    private fun expandTypeNames(
        declaredName: String,
        typeMirror: TypeMirror,
        nullable: Boolean = false,
        typeParameters: Map<String, PortableType>
    ): PortableType {
        return when {
            typeMirror is DeclaredType && typeMirror.typeArguments.isNotEmpty() -> {
                val typeName = typeMirror.asTypeName() as ParameterizedTypeName
                val params = typeName.typeArguments.mapIndexed { index, typeArgument ->
                    val name = when (typeArgument) {
                        is WildcardTypeName -> "${typeArgument.outTypes.first()}"
                        else -> "$typeArgument"
                    }
                    typeParameters[name] ?: expandTypeNames(
                        name,
                        typeMirror.typeArguments[index],
                        typeName.isNullable,
                        typeParameters
                    )
                }

                PortableType(
                    declaredName,
                    typeName.rawType.parameterizedBy(params.map(PortableType::name)),
                    typeMirror,
                    nullable || typeName.isNullable,
                    params
                )
            }
            else -> {
                val typeName = typeMirror.asTypeName()
                PortableType(declaredName, typeName, typeMirror, nullable || typeName.isNullable)
            }
        }
    }

}
package io.qalipsis.api.processors.kapt.serialization

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.lang.model.type.TypeMirror

@KotlinPoetMetadataPreview
internal data class SerializableProperty(
    val name: String,
    var type: PortableType,
    val isMutable: Boolean,
    val hasSetter: Boolean
)

data class PortableType(
    val declaredName: String,
    val name: TypeName,
    val type: TypeMirror,
    var isNullable: Boolean = false,
    val parameters: List<PortableType> = emptyList()
)
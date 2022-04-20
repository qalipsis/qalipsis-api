package io.qalipsis.api.processors.kapt.serialization

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import io.qalipsis.api.processors.kapt.AnnotationUtils
import io.qalipsis.api.processors.kapt.TypeUtils
import io.qalipsis.api.serialization.Portable
import io.qalipsis.api.services.ServicesFiles
import java.nio.file.Path
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic
import javax.tools.StandardLocation
import kotlin.io.path.Path


@KotlinPoetMetadataPreview
@ExperimentalStdlibApi
@DelicateKotlinPoetApi("Awareness of delicate aspect")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes(PortableProcessor.ANNOTATION_CLASS_NAME)
@SupportedOptions(
    PortableProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME
)
internal class PortableProcessor : AbstractProcessor() {

    companion object {

        const val ANNOTATION_CLASS_NAME = "io.qalipsis.api.serialization.Portable"

        const val SERIALIZERS_PATH = "META-INF/qalipsis/in-cluster-serializers"

        // Property pointing to the folder where Kapt generates sources.
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

    }

    private lateinit var typeUtils: TypeUtils

    private lateinit var elementUtils: Elements

    private lateinit var annotationUtils: AnnotationUtils

    private lateinit var builtInPortableTypesDefinitions: BuiltInPortableTypesDefinitions

    private val portableTypeHelper = PortableTypeHelper()

    /**
     * Path of the root folder when the Kotlin code has to be generated.
     */
    private lateinit var kaptKotlinGeneratedDir: Path

    /**
     * Qualified names of all the serializable classes.
     */
    private val allSerializers = mutableSetOf<String>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elementUtils = processingEnv.elementUtils
        typeUtils = TypeUtils(processingEnv.elementUtils, processingEnv.typeUtils)
        annotationUtils = AnnotationUtils(typeUtils)
        builtInPortableTypesDefinitions =
            BuiltInPortableTypesDefinitions(processingEnv.elementUtils, processingEnv.typeUtils, typeUtils)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        kaptKotlinGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.let { Path(it) } ?: return false

        // Processes the classes having the native Kotlin annotation.
        roundEnv.getElementsAnnotatedWith(Portable::class.java)
            .asSequence()
            .flatMap {
                if (it.kind == ElementKind.CLASS && Modifier.ABSTRACT !in (it as TypeElement).modifiers) {
                    listOf(it)
                } else {
                    val annotation = it.getAnnotation(Portable::class.java)
                    annotationUtils.getSupportedTypes { annotation.types }.filter {
                        Modifier.ABSTRACT !in it.modifiers
                    }
                }
            }
            .forEach(this::createSerializer)

        // Updates the file with the list of serializers.
        if (roundEnv.processingOver()) {
            ServicesFiles.writeFile(
                allSerializers,
                processingEnv.filer.createResource(StandardLocation.CLASS_OUTPUT, "", SERIALIZERS_PATH)
                    .openOutputStream()
            )
            allSerializers.clear()
        }

        return true
    }

    private fun createSerializer(serializableType: TypeElement) {
        processingEnv.messager.printMessage(
            Diagnostic.Kind.NOTE,
            "Creating portable serializer for: ${serializableType.qualifiedName}"
        )

        val className = serializableType.asClassName()
        val serializerClassName = "${className.simpleName}_PortableSerializer"
        var valueType: TypeName = className
        if (serializableType.typeParameters.isNotEmpty()) {
            valueType = className.parameterizedBy(serializableType.typeParameters.map { STAR })
        }

        val serializer = FunSpec.builder("serialize")
            .addParameter("value", valueType)
            .addParameter("writer", ClassName("io.qalipsis.api.serialization.portable", "Writer"))
        val deserializer = FunSpec.builder("deserialize")
            .addParameter("reader", ClassName("io.qalipsis.api.serialization.portable", "Reader"))
            .returns(valueType)

        createSerializer(serializableType, serializer, deserializer)

        val packageName = elementUtils.getPackageOf(serializableType).toString()
        val classFile = FileSpec.builder(packageName, serializerClassName)
            .addImport(packageName, className.simpleName)
            .addImport("io.qalipsis.api.serialization.portable", "Writer")
            .addImport("io.qalipsis.api.serialization.portable", "Reader")
            .addType(
                TypeSpec.classBuilder(serializerClassName)
                    .addModifiers(KModifier.INTERNAL)
                    .addFunction(serializer.build())
                    .addFunction(deserializer.build())
                    .build()
            )

        classFile.build()
        classFile.build().writeTo(kaptKotlinGeneratedDir)
    }

    private fun createSerializer(
        serializableType: TypeElement,
        serializer: FunSpec.Builder,
        deserializer: FunSpec.Builder
    ) {
        // FIXME The collection of types and properties should start from the uppest parent of the class.
        val (typeParameters, propertiesByName) = collectTypesAndProperties(serializableType)

        val constructorParameters = serializableType.enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }
            .asSequence()
            .map { it as ExecutableElement }
            .map { constructor ->
                constructor.parameters.asSequence()
                    .map { findMatchingProperty(it, propertiesByName) }
                    .toList()
            }
            .filterNot { null in it } // All the arguments should be mapped to a property.
            .maxByOrNull { it.size } ?: emptyList()


        val remainingProperties = propertiesByName.keys.toMutableSet()
        val constructorArguments = mutableListOf<String>()
        constructorParameters.filterNotNull().forEach {
            val statements = builtInPortableTypesDefinitions.getStatements(it)
            serializer.addStatement("writer.${statements.writer}")
            constructorArguments += "${it.name} = reader.${statements.reader}"
            remainingProperties.remove(it.name)
        }

        val setters = mutableListOf<String>()
        remainingProperties.mapNotNull { propertiesByName[it] }
            .filter { it.hasSetter || it.isMutable }
            .forEach {
                val statements = builtInPortableTypesDefinitions.getStatements(it)
                serializer.addStatement("writer.${statements.writer}")
                setters += if (it.isMutable) {
                    "${it.name} = reader.${statements.reader}"
                } else {
                    "set${it.name.replaceFirstChar { it.uppercase() }}(reader.${statements.reader})"
                }
                remainingProperties.remove(it.name)
            }

        var serializableTypeClassName = serializableType.asType().asTypeName()
        serializableTypeClassName = if (serializableTypeClassName is ParameterizedTypeName) {
            serializableTypeClassName.rawType.parameterizedBy(serializableTypeClassName.typeArguments.map { typeParameters["$it"]!!.name })
        } else {
            serializableTypeClassName
        }

        deserializer.addStatement(
            "return ${serializableTypeClassName}(${constructorArguments.joinToString()}).apply { ${
                setters.joinToString("\n", prefix = "\n")
            } }"
        )
    }

    private fun collectTypesAndProperties(serializableType: TypeElement): Pair<MutableMap<String, PortableType>, Map<String, SerializableProperty>> {
        val typeParameters = portableTypeHelper.expandJavaTypeParameters(serializableType.typeParameters).toMutableMap()
        val kotlinMetadataClass = kotlin.runCatching { serializableType.toImmutableKmClass() }.getOrNull()
        // Try to add Kotlin relevant metadata if the class is a Kotlin one.
        if (kotlinMetadataClass != null) {
            portableTypeHelper.expandKotlinTypeParameters(kotlinMetadataClass.typeParameters, typeParameters)
        }

        val propertiesByName = portableTypeHelper.extractJavaProperties(serializableType, typeParameters)
        // Try to add Kotlin exact types on the properties if the class is a Kotlin one.
        kotlinMetadataClass?.properties?.forEach { property ->
            propertiesByName[property.name]?.let { serializableProperty ->
                val type = serializableProperty.type
                serializableProperty.type =
                    portableTypeHelper.createTypeName(property.returnType, type, typeParameters)
            }
        }
        return Pair(typeParameters, propertiesByName)
    }

    private fun findMatchingProperty(
        parameter: VariableElement,
        propertiesByName: Map<String, SerializableProperty>
    ): SerializableProperty? {
        val name = "${parameter.simpleName}"
        return propertiesByName[name]?.takeIf { property ->
            processingEnv.typeUtils.isAssignable(property.type.type, parameter.asType())
        }
    }

}
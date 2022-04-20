package io.qalipsis.api.processors.kapt.serialization

import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.typeNameOf
import io.qalipsis.api.processors.kapt.AnnotationUtils
import io.qalipsis.api.processors.kapt.TypeUtils
import io.qalipsis.api.serialization.Serializable
import io.qalipsis.api.services.ServicesFiles
import java.nio.file.Path
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic
import javax.tools.StandardLocation
import kotlin.io.path.Path
import kotlin.reflect.KClass


@KotlinPoetMetadataPreview
@ExperimentalStdlibApi
@DelicateKotlinPoetApi("Awareness of delicate aspect")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes(
    SerializationAnnotationProcessor.ANNOTATION_CLASS_NAME,
    SerializationAnnotationProcessor.SERIALIZABLE_CLASS_NAME
)
@SupportedOptions(
    SerializationAnnotationProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME
)
internal class SerializationAnnotationProcessor : AbstractProcessor() {

    companion object {

        const val ANNOTATION_CLASS_NAME = "io.qalipsis.api.serialization.Serializable"

        const val SERIALIZABLE_CLASS_NAME = "kotlinx.serialization.Serializable"

        const val SERIALIZERS_PATH = "META-INF/qalipsis/serializers"

        // Property pointing to the folder where Kapt generates sources.
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private lateinit var typeUtils: TypeUtils

    private lateinit var elementUtils: Elements

    private lateinit var annotationUtils: AnnotationUtils

    private lateinit var stringElememt: TypeElement

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
        stringElememt = elementUtils.getTypeElement("java.lang.String")
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        kaptKotlinGeneratedDir =
            processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.let { Path(it) } ?: return false

        // Processes the classes having the native Kotlin annotation.
        roundEnv.getElementsAnnotatedWith(kotlinx.serialization.Serializable::class.java)
            .filter { it.kind == ElementKind.CLASS }
            .map { it as TypeElement }
            .forEach(this::createWrapper)

        // Processes the elements having the native Kotlin annotation.
        roundEnv.getElementsAnnotatedWith(Serializable::class.java).forEach(this::proceedQalipsisSerialization)

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

    private fun proceedQalipsisSerialization(annotatedType: Element) {
        val annotation = annotatedType.getAnnotation(Serializable::class.java)
        annotationUtils.getSupportedTypes { annotation.types }
            .filter { element ->
                (element == stringElememt
                        || element.getAnnotationsByType(kotlinx.serialization.Serializable::class.java).isNotEmpty())
                    .also { serializable ->
                        if (!serializable) {
                            processingEnv.messager.printMessage(
                                Diagnostic.Kind.WARNING,
                                "Cannot generate the SerialFormatWrapper for the class $element, because it does not have the @kotlinx.serialization.Serializable annotation"
                            )
                        }
                    }
            }
            .forEach(this::createWrapper)
    }

    private fun createWrapper(annotatedType: TypeElement) {
        val hasNoGeneric = annotatedType.typeParameters.isEmpty()
        if (hasNoGeneric) {
            val packageName = elementUtils.getPackageOf(annotatedType)
            val serializationWrapperClassName = "${annotatedType.simpleName}SerializationWrapper"
            generateKotlinWrapper(packageName, serializationWrapperClassName, annotatedType)
            allSerializers += "${packageName}.${serializationWrapperClassName}"
        } else {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Cannot generate the SerialFormatWrapper for the class $annotatedType, because it uses generics"
            )
        }
    }

    private fun generateKotlinWrapper(
        packageName: PackageElement,
        serializationWrapperClassName: String,
        annotatedType: TypeElement
    ) {
        processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, "Generating serialization wrapper for $annotatedType")
        val serializationWrapperClassFile = FileSpec.builder("$packageName", serializationWrapperClassName)
            .addImport("kotlinx.serialization", "decodeFromString")
            .addImport("kotlinx.serialization", "encodeToString")
            .addImport("io.qalipsis.api.serialization", "Serializers")
            .addType(
                TypeSpec.classBuilder(serializationWrapperClassName)
                    .addAnnotation(ClassName.bestGuess("kotlinx.serialization.ExperimentalSerializationApi"))
                    .addSuperinterface(
                        ClassName.bestGuess("io.qalipsis.api.serialization.SerialFormatWrapper")
                            .plusParameter(annotatedType.asClassName())
                    )
                    .addFunction(
                        FunSpec.builder("serialize")
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter(ParameterSpec.builder("entity", annotatedType.asClassName()).build())
                            .addStatement("return Serializers.json.encodeToString(entity).encodeToByteArray()")
                            .returns(ClassName("kotlin", "ByteArray"))
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("deserialize")
                            .addModifiers(KModifier.OVERRIDE)
                            .addParameter(ParameterSpec.builder("source", ClassName("kotlin", "ByteArray")).build())
                            .addStatement("return Serializers.json.decodeFromString(source.decodeToString())")
                            .returns(annotatedType.asClassName())
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("types", ARRAY.plusParameter(typeNameOf<KClass<*>>()), KModifier.OVERRIDE)
                            .initializer(CodeBlock.of("""arrayOf(${annotatedType.asClassName()}::class)""")).build()
                    )
                    .addProperty(
                        PropertySpec.builder("qualifier", ClassName("kotlin", "String"), KModifier.OVERRIDE)
                            .initializer(CodeBlock.of(""""kjson"""")).build()
                    )
                    .build()
            )
        serializationWrapperClassFile.build()
        serializationWrapperClassFile.build().writeTo(kaptKotlinGeneratedDir)
    }

}
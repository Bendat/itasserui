@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package itasserui.common.serialization

import arrow.core.Failure
import arrow.core.Success
import arrow.core.Try
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperConfig
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import itasserui.common.logger.Logger

object Serializer : Logger {
    val jackson = Jackson
    val mapper: ObjectMapper = ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .registerModule(KotlinModule())
        .configure(SerializationFeature.INDENT_OUTPUT, true)
        .registerModule(Serializer.Jackson.InlineModule)

    fun toJson(obj: Any?): String =
        mapper.writeValueAsString(obj)

    inline fun <reified T> fromJson(value: String): T =
        mapper.readValue(value, T::class.java)

    fun tryToJson(obj: Any?) =
        Try { toJson(obj) }

    inline fun <reified T> tryFromJson(value: String) =
        Try { fromJson<T>(value) }

    fun logJson(obj: () -> Any?) {
        when (val json = tryToJson(obj())) {
            is Success -> trace { json }
            is Failure -> warn { json }
        }
    }

    /**
     * Nitrite DB uses jackson for mapping values to the object repository.
     * This does not work with inline classes.
     *
     * This object includes a module to handle serializing inline classes and can
     * be used by Nitrite as a a facade.
     */
    object Jackson {
        val mapper: ObjectMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(InlineModule)

        fun toJson(obj: Any): String =
            mapper.writeValueAsString(obj)

        inline fun <reified T> fromJson(value: String): T =
            mapper.readValue(value, T::class.java)

        object InlineModule : SimpleModule("Inline") {
            override fun setupModule(context: SetupContext) {
                super.setupModule(context)
                context.appendAnnotationIntrospector(InlineAnnotationIntrospector)
            }

            @Suppress("SpellCheckingInspection")
            object InlineAnnotationIntrospector : NopAnnotationIntrospector() {
                override fun findCreatorAnnotation(config: MapperConfig<*>, a: Annotated): JsonCreator.Mode? {
                    if (a is AnnotatedMethod && a.name == "box-impl") {
                        return JsonCreator.Mode.DEFAULT
                    }
                    return null
                }
            }
        }
    }
}


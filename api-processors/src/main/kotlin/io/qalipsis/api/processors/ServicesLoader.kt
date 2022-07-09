package io.qalipsis.api.processors

import io.qalipsis.api.processors.injector.Injector
import io.qalipsis.api.services.ServicesFiles

/**
 *
 * @author Eric Jessé
 */
object ServicesLoader {

    /**
     * Loads the scenarios passing the injector as parameter.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> loadScenarios(injector: Injector): Collection<T> {
        return this.javaClass.classLoader.getResources("META-INF/qalipsis/scenarios")
            .toList()
            .flatMap { ServicesFiles.readFile(it.openStream()) }
            .map { loaderClass ->
                try {
                    Class.forName(loaderClass).getConstructor(Injector::class.java)
                        .newInstance(injector) as T
                } catch (e: NoSuchMethodException) {
                    Class.forName(loaderClass).getConstructor().newInstance() as T
                }
            }
    }

    /**
     * Loads the profiles defined in the plugins.
     */
    fun loadPlugins(): Collection<String> {
        return this.javaClass.classLoader.getResources("META-INF/qalipsis/plugin")
            .toList()
            .flatMap { ServicesFiles.readFile(it.openStream()) }
            .flatMap { it.split(",") }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()
    }

}

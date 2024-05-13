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

package io.qalipsis.api.meters

import io.qalipsis.api.context.ScenarioName
import io.qalipsis.api.context.StepName
import java.util.function.ToDoubleFunction

/**
 * Campaign lifecycle relevant meter registry.
 *
 * @author Eric Jessé
 *
 */
interface CampaignMeterRegistry {

    /**
     * Creates a new [Counter] metric to be added to the registry. This metric measures the
     * count of specific events collected over time.
     *
     * @param scenarioName the name of the scenario under which the count is collected
     * @param stepName the name of a step within the scenario
     * @param name the name of the counter metric
     * @param tags additional key-value pairs to associate with the counter metric
     *
     * @sample counterExample
     */
    fun counter(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
    ): Counter

    /**
     * Creates a new [Timer] metric to be added to the registry. This metric measures the duration of an operation or a task.
     *
     * @param scenarioName the name of the scenario under which the timer is recorded
     * @param stepName the name of a step within the scenario
     * @param name the name of the timer metric
     * @param tags additional key-value pairs to associate with the timer metric
     * @param percentiles a list of values within the range of 1.0-100.0, representing specific points of observation, defaults to a list of 50.0, 75.0 and 99.9
     * @param histogramCounts a list of values, whose frequencies within a bucket/bin in a histogram are to be observed, defaults to an empty list
     * @param minHistogramBoundary minimum boundary within which a histogram should be created, defaults to 10.0
     * @param maxHistogramBoundary maximum boundary within which a histogram should be created, defaults to 100.0
     * @param compressionFactor determines the level of compression applied to the data stored
     * i.e. how much the data is compacted to reduce memory usage. It defaults to 100.0.
     *
     * @sample timerExample
     */
    fun timer(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
        percentiles: Collection<Double> = listOf(50.0, 75.0, 99.9),
        histogramCounts: Collection<Double> = emptyList(),
        minHistogramBoundary: Double = 1000.0,
        maxHistogramBoundary: Double = 10000.0,
        compressionFactor: Double = 100.0,
    ): Timer

    /**
     * Creates a new [Gauge] metric to be added to the registry. This metric tracks instantaneous values
     * change over time.
     *
     * @param scenarioName the name of the scenario under which the gauge is collected
     * @param stepName the name of a step within the scenario
     * @param name the name of the gauge metric
     * @param tags additional key-value pairs to associate with the gauge metric
     * @param stateObject contains additional contextual metadata for the gauge
     * @param valueFunction function that is applied to compute the value of the gauge based on the state object
     *
     * @sample gaugeExample2
     */
    fun <T> gauge(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
        stateObject: T,
        valueFunction: ToDoubleFunction<T>,
    ): Gauge

    /**
     * Creates a new [Gauge] metric to be added to the registry. This metric tracks instantaneous values
     * change over time.
     *
     * @param scenarioName the name of the scenario under which the gauge is collected
     * @param stepName the name of a step within the scenario
     * @param name the name of the gauge metric
     * @param tags additional key-value pairs to associate with the gauge metric
     * @param number the numeric value to be monitored by the gauge
     *
     * @sample gaugeExample1
     */
    fun <T : Number> gauge(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
        number: T,
    ): Gauge

    /**
     * Creates a new [Gauge] metric to be added to the registry. This metric tracks instantaneous values
     * change over time.
     *
     * @param scenarioName the name of the scenario under which the gauge is collected
     * @param stepName the name of a step within the scenario
     * @param name the name of the gauge metric
     * @param tags additional key-value pairs to associate with the gauge metric
     * @param collection the collection whose size is to be monitored by the gauge
     *
     * @sample gaugeCollectionSizeExample
     */
    fun <T : Collection<*>> gaugeCollectionSize(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
        collection: T,
    ): Gauge

    /**
     * Creates a new [Gauge] metric to be added to the registry. This metric tracks instantaneous values
     * change over time.
     *
     * @param scenarioName the name of the scenario under which the gauge is collected
     * @param stepName the name of a step within the scenario
     * @param name the name of the gauge metric
     * @param tags additional key-value pairs to associate with the gauge metric
     * @param map the map whose size is to be monitored by the gauge
     *
     * @sample gaugeMapSizeExample
     */
    fun <T : Map<*, *>> gaugeMapSize(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
        map: T,
    ): Gauge

    /**
     * Creates a new [DistributionSummary] metric to be added to the registry. This metric
     * provides statistical data about the values observed/collected from an operation.
     *
     * @param scenarioName the name of the scenario under which the summary is collected
     * @param stepName the name of a step within the scenario
     * @param name the name of the summary metric
     * @param tags additional key-value pairs to associate with the summary metric
     * @param percentiles a list of values within the range of 1.0-100.0, representing specific points of observation, defaults to a list of 50.0, 75.0 and 99.9
     * @param histogramCounts a list of values, whose frequencies within a bucket/bin in a histogram are to be observed, defaults to null
     * @param minHistogramBoundary minimum boundary within which a histogram should be created, defaults to 10.0
     * @param maxHistogramBoundary maximum boundary within which a histogram should be created, defaults to 100.0
     * @param compressionFactor determines the level of compression applied to the data stored
     * i.e. how much the data is compacted to reduce memory usage. It defaults to 100.0.
     *
     * @sample summaryExample
     */
    fun summary(
        scenarioName: ScenarioName = "",
        stepName: StepName = "",
        name: String,
        tags: Map<String, String> = emptyMap(),
        percentiles: Collection<Double> = listOf(50.0, 75.0, 99.9),
        histogramCounts: Collection<Double> = emptyList(),
        minHistogramBoundary: Double = 100.0,
        maxHistogramBoundary: Double = 1000.0,
        compressionFactor: Double = 100.0,
    ): DistributionSummary

    /**
     * Example usage of the `counter` function.
     */
    private fun counterExample() {
        counter(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "counter name",
            tags = mapOf("foo" to "bar")
        )
    }

    /**
     * Example usage of the `gauge` function with a number param. The number in this scenario,
     * 100 marks the value to be tracked by the gauge.
     */
    private fun gaugeExample1() {
        gauge(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "gauge name",
            tags = mapOf("tag-1" to "value-1", "tag-2" to "value-2"),
            number = 1000,
        )
    }

    /**
     * Example usage of the `gauge` function using a stateObject and a value function.
     */
    private fun gaugeExample2() {
        gauge(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "gauge name",
            tags = mapOf("tag-1" to "value-1", "tag-2" to "value-2"),
            stateObject = 25,
            valueFunction = { size -> size.toDouble() },
        )
    }

    /**
     * Example usage of the `gaugeCollectionSize` function.
     */
    private fun gaugeCollectionSizeExample() {
        gaugeCollectionSize(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "devices",
            tags = mapOf("tag-1" to "value-1", "tag-2" to "value-2"),
            collection = listOf("device-a", "device-b", "device-c"),
        )
    }

    /**
     * Example usage of the `gaugeMapSize` function.
     */
    private fun gaugeMapSizeExample() {
        gaugeMapSize(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "device count",
            tags = mapOf("tag-1" to "value-1", "tag-2" to "value-2"),
            map = mapOf("device-a" to 10, "device-b" to 5, "device-c" to 8),
        )
    }


    /**
     * Example usage of the `timer` function with default values for percentiles, histogramCounts,
     * minHistogramBoundary, maxHistogramBoundary, compressionFactor.
     */
    private fun timerExample() {
        timer(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "http-requests duration",
            tags = mapOf("environment" to "production", "region" to "us-west"),
            percentiles = listOf(25.0, 50.0, 75.0, 99.9),
            histogramCounts = listOf(7000.0, 10000.0),
            minHistogramBoundary = 1000.0,
            maxHistogramBoundary = 10000.0,
            compressionFactor = 10.0
        )
    }

    /**
     * Example usage of the `summary` function with default values for percentiles, histogramCounts,
     * minHistogramBoundary, maxHistogramBoundary, compressionFactor.
     */
    private fun summaryExample() {
        summary(
            scenarioName = "scenario 1",
            stepName = "step 1",
            name = "requests spread",
            tags = mapOf("foo" to "bar", "region" to "us-east"),
            percentiles = listOf(25.0, 50.0, 75.0, 99.9),
            histogramCounts = listOf(7000.0, 10000.0),
            minHistogramBoundary = 1000.0,
            maxHistogramBoundary = 10000.0,
            compressionFactor = 10.0
        )
    }

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun counter(name: String, tags: Map<String, String>): Counter

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun counter(name: String, vararg tags: String): Counter

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun summary(name: String, tags: Map<String, String>): DistributionSummary

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun summary(name: String, vararg tags: String): DistributionSummary

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun timer(name: String, tags: Map<String, String>): Timer

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun timer(name: String, vararg tags: String): Timer

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun <T> gauge(
        name: String,
        tags: Map<String, String>,
        stateObject: T,
        valueFunction: ToDoubleFunction<T>,
    ): T

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun <T : Number> gauge(name: String, tags: Map<String, String>, number: T): T

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun <T : Collection<*>> gaugeCollectionSize(name: String, tags: Map<String, String>, collection: T): T

    @Deprecated(message = "Use the function with the scenario and step as argument")
    fun <T : Map<*, *>> gaugeMapSize(name: String, tags: Map<String, String>, map: T): T

    fun clear()
}
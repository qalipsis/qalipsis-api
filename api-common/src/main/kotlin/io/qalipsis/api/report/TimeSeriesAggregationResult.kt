package io.qalipsis.api.report

import io.micronaut.core.annotation.Introspected
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

/**
 * Result of the aggregation of time-series data generated during campaign executions.
 *
 * @property start start instant of the aggregated bucket
 * @property elapsed elapsed time between the start of the aggregation and the start of this result
 * @property value numeric result of the aggregation
 *
 * @author Eric Jessé
 */
@Introspected
@Schema(
    name = "Result of an aggregation of time-series data",
    description = "Single point of result of an aggregation of time-series data generated during campaign executions"
)
data class TimeSeriesAggregationResult(
    @field:Schema(description = "Start of the aggregation bucket")
    val start: Instant,

    @field:Schema(description = "Elapsed time between the start of the aggregation and the start of this result")
    val elapsed: Duration,

    @field:Schema(description = "Numeric result of the aggregation")
    val value: BigDecimal? = null
)

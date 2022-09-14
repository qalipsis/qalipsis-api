package io.qalipsis.api.campaign

import io.micronaut.core.annotation.Introspected
import io.qalipsis.api.executionprofile.CompletionMode
import io.qalipsis.api.executionprofile.MinionsStartingLine
import io.qalipsis.api.executionprofile.Stage
import jakarta.inject.Singleton

/**
 * Interface of execution profile configuration.
 *
 * @author Svetlana Paliashchuk
 */
@Introspected
interface ExecutionProfileConfiguration

@Singleton
@Introspected
class RegularExecutionProfileConfiguration(
    val periodInMs: Long,
    val minionsCountProLaunch: Int
) : ExecutionProfileConfiguration

@Singleton
@Introspected
class AcceleratingExecutionProfileConfiguration(
    val startPeriodMs: Long, val accelerator: Double,
    val minPeriodMs: Long, val minionsCountProLaunch: Int
) : ExecutionProfileConfiguration

@Singleton
@Introspected
class ProgressiveVolumeExecutionProfileConfiguration(
    val periodMs: Long, val minionsCountProLaunchAtStart: Int,
    val multiplier: Double, val maxMinionsCountProLaunch: Int
) : ExecutionProfileConfiguration

@Singleton
@Introspected
class StageExecutionProfileConfiguration(
    val stages: List<Stage>,
    val completion: CompletionMode
) : ExecutionProfileConfiguration

@Singleton
@Introspected
class TimeFrameExecutionProfileConfiguration(
    val periodInMs: Long,
    val timeFrameInMs: Long
) : ExecutionProfileConfiguration

@Singleton
@Introspected
class UserDefinedExecutionProfileConfiguration(
    val specification: (pastPeriodMs: Long, totalMinionsCount: Int, speedFactor: Double) -> MinionsStartingLine
) : ExecutionProfileConfiguration

@Singleton
@Introspected
class DefaultExecutionProfileConfiguration : ExecutionProfileConfiguration

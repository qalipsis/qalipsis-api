package io.qalipsis.api.steps.datasource

import io.qalipsis.api.context.StepContext
import io.qalipsis.api.context.StepName
import io.qalipsis.api.context.StepStartStopContext
import io.qalipsis.api.logging.LoggerHelper.logger
import io.qalipsis.api.steps.AbstractStep
import java.util.concurrent.atomic.AtomicLong

/**
 * General purpose step to read data from a source sequentially, transform and forward.
 *
 * @property reader the reader providing the raw values in an iterative way
 * @property processor validates and transforms the raw values individually
 * @property converter converts the raw values to forward them individually to next steps
 *
 * @param R raw type returned by [reader]
 * @param T intermediate type from [processor] to [converter]
 * @param O output type returned by the step
 *
 * @author Eric Jessé
 */
class SequentialDatasourceStep<R, T, O>(
    name: StepName,
    private val reader: DatasourceIterativeReader<R>,
    private val processor: DatasourceObjectProcessor<R, T>,
    private val converter: DatasourceObjectConverter<T, O>
) : AbstractStep<Unit, O>(name, null) {

    private val rowIndex = AtomicLong()

    override suspend fun start(context: StepStartStopContext) {
        log.trace { "Starting datasource reader for step $name" }
        rowIndex.set(0)
        converter.start(context)
        processor.start(context)
        reader.start(context)
    }

    override suspend fun stop(context: StepStartStopContext) {
        log.trace { "Stopping datasource reader for step $name" }
        kotlin.runCatching {
            reader.stop(context)
        }
        kotlin.runCatching {
            processor.stop(context)
        }
        kotlin.runCatching {
            converter.stop(context)
        }
        log.trace { "Datasource reader stopped for step $name" }
    }

    override suspend fun execute(context: StepContext<Unit, O>) {
        log.trace { "Sequential datasource reader for step $name" }
        if (reader.hasNext()) {
            val value = processor.process(rowIndex, reader.next())
            log.trace { "Received one record" }
            converter.supply(rowIndex, value, context)
        } else {
            context.isTail = true
        }
    }

    companion object {

        private val log = logger()

    }
}

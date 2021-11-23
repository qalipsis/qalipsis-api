package io.qalipsis.api.steps

import cool.graph.cuid.Cuid
import io.micronaut.core.annotation.Introspected
import io.qalipsis.api.context.CorrelationRecord
import io.qalipsis.api.context.StepName
import io.qalipsis.api.scenario.ScenarioSpecification
import java.time.Duration
import javax.validation.constraints.NotBlank

/**
 * Specification for a [io.qalipsis.core.factory.steps.LeftJoinStep].
 *
 * @author Eric Jessé
 */
@Introspected
data class InnerJoinStepSpecification<INPUT, OUTPUT>(
    val primaryKeyExtractor: (CorrelationRecord<INPUT>) -> Any?,
    val secondaryKeyExtractor: (CorrelationRecord<out Any?>) -> Any?,
    val secondaryStepName: @NotBlank StepName
) : AbstractStepSpecification<INPUT?, OUTPUT?, InnerJoinStepSpecification<INPUT, OUTPUT>>() {
    var cacheTimeout: Duration = Duration.ZERO
}

/**
 * Correlates the records coming from an earlier step with the ones generated by the specified specification.
 * Correlation keys are used in order to be able to map the record from each source together.
 *
 * @param using function to execute on the records from the local earlier step to extract the correlation key.
 * @param on specification of the step that should generate the remote records.
 * @param having function to execute on the remote records to extract the correlation key.
 *
 * @author Eric Jessé
 */
fun <INPUT, OTHER_INPUT> StepSpecification<*, INPUT, *>.innerJoin(
    using: (CorrelationRecord<INPUT>) -> Any?,
    on: (scenario: ScenarioSpecification) -> StepSpecification<*, OTHER_INPUT, *>,
    having: (CorrelationRecord<OTHER_INPUT>) -> Any?
): InnerJoinStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>> {

    // Since the relationship is performed in the step name, we generate one in case it is not specified by the user.
    val secondaryStep = on(scenario)
    if (secondaryStep.name.isBlank()) {
        secondaryStep.name = Cuid.createCuid()
    }
    // We force the step to be known by the scenario.
    scenario.register(secondaryStep)

    @Suppress("UNCHECKED_CAST")
    val step = InnerJoinStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>>(
        using,
        having as CorrelationRecord<*>.() -> Any?,
        secondaryStep.name
    )
    this.add(step)
    return step
}

/**
 * Correlates the records coming from an earlier step with the ones generated by step with the specified name.
 * Correlation keys are used in order to be able to map the record from each source together.
 *
 * @param using function to execute on the records from the local earlier step to extract the correlation key.
 * @param on name of the step, which should have be specified earlier in the same scenario.
 * @param having function to execute on the remote records to extract the correlation key.
 *
 * @author Eric Jessé
 */
fun <INPUT, OTHER_INPUT> StepSpecification<*, INPUT, *>.innerJoin(
    using: (CorrelationRecord<INPUT>) -> Any?, on: String,
    having: (CorrelationRecord<OTHER_INPUT>) -> Any?
): InnerJoinStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>> {

    @Suppress("UNCHECKED_CAST")
    val step = InnerJoinStepSpecification<INPUT, Pair<INPUT, OTHER_INPUT>>(
        using,
        having as CorrelationRecord<out Any?>.() -> Any?,
        on
    )
    this.add(step)
    return step
}

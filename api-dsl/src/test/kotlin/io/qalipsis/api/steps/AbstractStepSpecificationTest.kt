package io.qalipsis.api.steps

import assertk.all
import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import assertk.assertions.prop
import io.qalipsis.test.mockk.relaxedMockk
import org.junit.jupiter.api.Test
import java.time.Duration

internal class AbstractStepSpecificationTest {

    @Test
    internal fun `should build a default step`() {
        // when
        val specification = TestAbstractStepSpecification()

        // then
        assertThat(specification).all {
            prop(TestAbstractStepSpecification::retryPolicy).isNull()
            prop(TestAbstractStepSpecification::reporting).prop(StepReportingSpecification::reportErrors).isFalse()
            prop(TestAbstractStepSpecification::selectors).isEmpty()
            prop(TestAbstractStepSpecification::iterations).isEqualTo(1)
            prop(TestAbstractStepSpecification::iterationPeriods).isEqualTo(Duration.ZERO)
            prop(TestAbstractStepSpecification::timeout).isNull()
            prop(TestAbstractStepSpecification::nextSteps).isEmpty()
        }
    }

    @Test
    internal fun `should enable the report`() {
        // given
        val specification = TestAbstractStepSpecification()

        // when
        specification.configure {
            report {
                reportErrors = true
            }
        }

        // then
        assertThat(specification).all {
            prop(TestAbstractStepSpecification::retryPolicy).isNull()
            prop(TestAbstractStepSpecification::reporting).prop(StepReportingSpecification::reportErrors).isTrue()
            prop(TestAbstractStepSpecification::selectors).isEmpty()
            prop(TestAbstractStepSpecification::iterations).isEqualTo(1L)
            prop(TestAbstractStepSpecification::iterationPeriods).isEqualTo(Duration.ZERO)
            prop(TestAbstractStepSpecification::timeout).isNull()
            prop(TestAbstractStepSpecification::nextSteps).isEmpty()
        }
    }

    @Test
    internal fun `should enable the selectors as map`() {
        // when
        val specification = TestAbstractStepSpecification()
        specification.runOn(mapOf("key1" to "value1", "key2" to "value2"))

        // then
        assertThat(specification).all {
            prop(TestAbstractStepSpecification::retryPolicy).isNull()
            prop(TestAbstractStepSpecification::reporting).prop(StepReportingSpecification::reportErrors).isFalse()
            prop(TestAbstractStepSpecification::selectors).isEqualTo(mapOf("key1" to "value1", "key2" to "value2"))
            prop(TestAbstractStepSpecification::iterations).isEqualTo(1L)
            prop(TestAbstractStepSpecification::iterationPeriods).isEqualTo(Duration.ZERO)
            prop(TestAbstractStepSpecification::timeout).isNull()
            prop(TestAbstractStepSpecification::nextSteps).isEmpty()
        }
    }

    @Test
    internal fun `should enable the selectors as vararg of pairs`() {
        // when
        val specification = TestAbstractStepSpecification()
        specification.runOn("key1" to "value1", "key2" to "value2")

        // then
        assertThat(specification).all {
            prop(TestAbstractStepSpecification::retryPolicy).isNull()
            prop(TestAbstractStepSpecification::reporting).prop(StepReportingSpecification::reportErrors).isFalse()
            prop(TestAbstractStepSpecification::selectors).isEqualTo(mapOf("key1" to "value1", "key2" to "value2"))
            prop(TestAbstractStepSpecification::iterations).isEqualTo(1L)
            prop(TestAbstractStepSpecification::iterationPeriods).isEqualTo(Duration.ZERO)
            prop(TestAbstractStepSpecification::timeout).isNull()
            prop(TestAbstractStepSpecification::nextSteps).isEmpty()
        }
    }

    @Test
    internal fun `should enable the iteration`() {
        // when
        val specification = TestAbstractStepSpecification()
        specification.iterate(123, Duration.ofSeconds(2))

        // then
        assertThat(specification).all {
            prop(TestAbstractStepSpecification::retryPolicy).isNull()
            prop(TestAbstractStepSpecification::reporting).prop(StepReportingSpecification::reportErrors).isFalse()
            prop(TestAbstractStepSpecification::selectors).isEmpty()
            prop(TestAbstractStepSpecification::iterations).isEqualTo(123L)
            prop(TestAbstractStepSpecification::iterationPeriods).isEqualTo(Duration.ofSeconds(2))
            prop(TestAbstractStepSpecification::timeout).isNull()
            prop(TestAbstractStepSpecification::nextSteps).isEmpty()
        }
    }

    @Test
    internal fun `should enable the timeout`() {
        // when
        val specification = TestAbstractStepSpecification()
        specification.timeout(2344)

        // then
        assertThat(specification).all {
            prop(TestAbstractStepSpecification::retryPolicy).isNull()
            prop(TestAbstractStepSpecification::reporting).prop(StepReportingSpecification::reportErrors).isFalse()
            prop(TestAbstractStepSpecification::selectors).isEmpty()
            prop(TestAbstractStepSpecification::iterations).isEqualTo(1)
            prop(TestAbstractStepSpecification::iterationPeriods).isEqualTo(Duration.ZERO)
            prop(TestAbstractStepSpecification::timeout).isEqualTo(Duration.ofMillis(2344))
            prop(TestAbstractStepSpecification::nextSteps).isEmpty()
        }
    }

    @Test
    internal fun `should inherit the selectors when not set in the next one`() {
        // when
        val specification = TestAbstractStepSpecification()
        specification.scenario = relaxedMockk()
        specification.runOn("key1" to "value1", "key2" to "value2")
        val next = TestAbstractStepSpecification()
        specification.add(next)

        // then
        assertThat(next).prop(TestAbstractStepSpecification::selectors)
            .isEqualTo(mapOf("key1" to "value1", "key2" to "value2"))
    }

    @Test
    internal fun `should not inherit the selectors when already set in the next one`() {
        // when
        val specification = TestAbstractStepSpecification()
        specification.scenario = relaxedMockk()
        specification.runOn("key1" to "value1", "key2" to "value2")
        val next = TestAbstractStepSpecification()
        next.runOn("key3" to "value3", "key4" to "value4")
        specification.add(next)

        // then
        assertThat(next).prop(TestAbstractStepSpecification::selectors)
            .isEqualTo(mapOf("key3" to "value3", "key4" to "value4"))
    }

    class TestAbstractStepSpecification : AbstractStepSpecification<Int, String, TestAbstractStepSpecification>()
}
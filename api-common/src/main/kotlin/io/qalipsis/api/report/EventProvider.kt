package io.qalipsis.api.report

import io.micronaut.context.annotation.Requires
import io.micronaut.validation.Validated
import io.qalipsis.api.report.query.QueryDescription
import javax.validation.constraints.Max
import javax.validation.constraints.Positive

/**
 * Interface of data provider for [io.qalipsis.api.events.Event]s.
 * The implementation should be located in each plugin supporting the persistence of events.
 *
 * @author Eric Jessé
 */
@Requires(env = ["standalone", "head"])
@Validated
interface EventProvider {

    /**
     * Searches names of events in the tenant, matching the filters if specified.
     *
     * @param tenant the reference of the tenant owning the data
     * @param filters the different filters (potentially with wildcard *) the names should match
     * @param size the maximum count of results to return
     */
    fun searchNames(tenant: String, filters: Collection<String>, @Positive @Max(100) size: Int): Collection<String>

    /**
     * List all the fields that can be used for aggregation of data on events.
     *
     * @param tenant the reference of the tenant owning the data
     */
    fun listFields(tenant: String): Collection<DataField>

    /**
     * Searches tags matching the potential filters and provide also values.
     *
     * @param tenant the reference of the tenant owning the data
     * @param filters the different filters (potentially with wildcard *) the tags names should match
     * @param size the maximum count of results of tags names and values for each name
     */
    fun searchTagsAndValues(tenant: String, filters: Collection<String>, @Positive @Max(100) size: Int): Map<String, Collection<String>>

    /**
     * Prepares the query on events and returns it wrapped into a JSON object containing potential additional metadata.
     */
    fun createQuery(tenant: String, query: QueryDescription): String

}
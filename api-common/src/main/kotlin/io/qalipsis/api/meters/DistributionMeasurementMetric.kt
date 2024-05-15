/*
 * QALIPSIS
 * Copyright (C) 2023 AERIS IT Solutions GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.qalipsis.api.meters

/**
 * Custom measurement metric adapted for measurements involving percentiles.
 * Holds the value of a statistical percentile, its observed measurement
 * alongside, the measured statistic.
 *
 * @property observationPoint a double value representing a statistical percentile[Statistic.PERCENTILE].
 *  It describes a point of interest in a statistical distribution to be evaluated.
 *
 * @author Francisca Eze
 */
class DistributionMeasurementMetric(
    override val value: Double,
    override val statistic: Statistic,
    val observationPoint: Double,
) : Measurement
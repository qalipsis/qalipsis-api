//package io.qalipsis.api.meters
//
//import io.qalipsis.api.Executors
//import io.qalipsis.api.lang.tryAndLogOrNull
//import io.qalipsis.api.logging.LoggerHelper.logger
//import jakarta.inject.Named
//import java.time.Duration
//import java.util.concurrent.CountDownLatch
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.sync.Mutex
//import kotlinx.coroutines.sync.withLock
//import kotlin.concurrent.fixedRateTimer
//
///**
// * Abstract implementation of [MeasurementPublisher] from which
// */
//abstract class AbstractMeasurementPublisherImpl(
//    //@TODO thinking to use a background scope in here
//    private val step: Duration,
//    @Named(Executors.BACKGROUND_EXECUTOR_NAME) private val coroutineScope: CoroutineScope,
//) : MeasurementPublisher {
//    private var publishingJob: Job? = null
//
//    private val mutex = Mutex(false)
//
//    private var snapshots = listOf<MeterSnapshot<*>>()
//
//    private val meters: MutableMap<Meter.Id, Meter<*>> = mutableMapOf()
//
////    override fun start() {
////        //assert that a previous job has been put to a stop
////        logger.info("")
////        require(publishingJob == null) { "" }
////        val countDownLatch = CountDownLatch(1)
////        publishingJob = coroutineScope.launch {
////            countDownLatch.countDown()
////            val stepMillis = step.toMillis()
////            val initialDelayMillis: Long = stepMillis - System.currentTimeMillis() % stepMillis + 1
////            fixedRateTimer(name = "", daemon = false, initialDelay = initialDelayMillis, period = stepMillis) {
////                launch {
////                    snapshots = takeSnapshots(meters).toList()
////                    logger.info("")
////                        mutex.withLock {
////                    publishSafely(snapshots)
////                    }
////                }
////            }
////        }
////        countDownLatch.await()
////    }
////
////
////    //@TODO fix this method and remove unnecessary duplicates
////    private suspend fun takeSnapshots(meters: Map<Meter.Id, Meter<*>>): Collection<MeterSnapshot<*>> {
////        val measurements = mutableListOf<MeterSnapshot<*>>()
////        for ((_, meter) in meters) {
////            val snapshot = when (meter) {
////                is Counter -> MeterSnapShotImpl(meter, meter.measure())
////                is Gauge -> MeterSnapShotImpl(meter, meter.measure())
////                //pass in time unit
////                is Timer -> {
////                    MeterSnapShotImpl(meter, meter.measure())
////                }
////
////                is DistributionSummary -> {
////                    MeterSnapShotImpl(meter, meter.measure())
////                }
////
////                else -> {
////                    throw Exception("Unknown Meter Type")
////                }
////            }
////            measurements.add(snapshot)
////        }
////        return measurements
////    }
//
//
//    override fun suspend stop() {
//        publishingJob?.cancel()
//        super.stop()
//    }
//
//    private suspend fun publishSafely(snapshots: Collection<MeterSnapshot<*>>) {
//        logger.trace { "Publishing ${snapshots.size} snapshots." }
//        tryAndLogOrNull(logger) {
//            mutex.withLock {
//                publish(snapshots)
//            }
//        }
//    }
//
//    companion object {
//        @JvmStatic
//        val logger = logger()
//    }
//}
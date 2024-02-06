package io.github.prismaplatform.common

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@JvmInline
@Serializable
value class Snowflake(val value: Long): Comparable<Snowflake> {
    constructor(
        timestamp: Long,
        nodeId: Long,
        clusterId: Long,
        sequence: Long
    ): this(timestamp shl (SnowflakeNode.TIMESTAMP_SHIFT) or (nodeId shl SnowflakeNode.NODE_SHIFT) or (clusterId shl SnowflakeNode.SEQUENCE_BITS) or sequence)

    val timeDifference: Duration
        get() = (value shr SnowflakeNode.TIMESTAMP_SHIFT).seconds

    val timestamp: Instant
        get() = Instant.fromEpochSeconds(SnowflakeNode.PRISMA_EPOCH + timeDifference.inWholeSeconds)

    val nodeId: Long
        get() = (value and SnowflakeNode.MASK_NODE_ID) shr SnowflakeNode.NODE_SHIFT

    val clusterId: Long
        get() = (value and SnowflakeNode.MASK_CLUSTER_ID) shr SnowflakeNode.SEQUENCE_BITS

    val sequence: Long
        get() = (value and SnowflakeNode.MASK_SEQUENCE)

    override fun compareTo(other: Snowflake): Int = value.compareTo(other.value)

    override fun toString(): String = value.toString()
}

class SnowflakeNode(private val clusterId: Long, private val nodeId: Long) {
    @Synchronized
    fun generate(): Snowflake {
        val timestamp = (System.currentTimeMillis() / 1000) - PRISMA_EPOCH
        val sequence = SnowflakeService.generateSequenceId(timestamp).toLong()

        return Snowflake(timestamp, nodeId, clusterId, sequence)
    }

    companion object {
        const val PRISMA_EPOCH = 164_038_400

        const val NODE_ID_BITS = 5
        const val CLUSTER_ID_BITS = 5
        const val SEQUENCE_BITS = 12

        const val TIMESTAMP_SHIFT = NODE_ID_BITS + CLUSTER_ID_BITS + SEQUENCE_BITS
        const val NODE_SHIFT = CLUSTER_ID_BITS + SEQUENCE_BITS

        const val MASK_NODE_ID = (1L shl NODE_ID_BITS) - 1 shl NODE_SHIFT
        const val MASK_CLUSTER_ID = (1L shl CLUSTER_ID_BITS) -1 shl SEQUENCE_BITS
        const val MASK_SEQUENCE = (1L shl SEQUENCE_BITS) - 1
    }
}

object SnowflakeAgeComparator: Comparator<Snowflake> {
    override fun compare(o1: Snowflake, o2: Snowflake): Int {
        val timeDifference = o1.timeDifference.compareTo(o2.timeDifference)
        if (timeDifference != 0) return timeDifference
        return o1.sequence.compareTo(o2.sequence)
    }
}

inline fun <reified T> snowflakeAgeComparator(crossinline selector: (T) -> Snowflake): Comparator<T> =
    Comparator { o1, o2 -> SnowflakeAgeComparator.compare(selector(o1), selector(o2)) }

class SnowflakeService(val cluster: Long) {
    private val nodes = ConcurrentHashMap<Long, SnowflakeNode>()

    fun nextNode() = node(Thread.currentThread().id)

    fun node(nodeId: Long): SnowflakeNode {
        val existing = nodes[nodeId]
        if (existing != null) return existing

        val new = SnowflakeNode(cluster, nodeId)
        nodes[nodeId] = new
        return new
    }

    fun getNode(nodeId: Long): SnowflakeNode? = nodes[nodeId]

    fun generate(): Snowflake =
        nextNode().generate()

    companion object {
        private val sequenceIdAtomic = AtomicInteger(0)
        private val lastTimestamp = AtomicLong(-1)

        fun generateSequenceId(timestamp: Long): Int =
            if (lastTimestamp.get() == timestamp) sequenceIdAtomic.incrementAndGet() else {
                sequenceIdAtomic.set(0)
                lastTimestamp.set(timestamp)
                0
            }
    }
}
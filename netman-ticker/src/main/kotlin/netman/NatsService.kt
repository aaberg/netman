package netman

import jakarta.inject.Singleton

@Singleton
class NatsService(private val natsClient: io.nats.client.Connection) {

    fun sendEmptyMessage(topic: String) {
        natsClient.publish(topic, ByteArray(0))
        println("Sent empty message to topic: $topic")
    }
}
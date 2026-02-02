package netman

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import jakarta.inject.Inject

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

@Command(name = "netman-ticker", description = ["Netman Ticker CLI for sending NATS messages"],
        mixinStandardHelpOptions = true)
class NetmanTickerCommand : Runnable {

    @Inject
    lateinit var natsService: NatsService

    @Option(names = ["-v", "--verbose"], description = ["Verbose mode"])
    private var verbose : Boolean = false

    @Parameters(index = "0", paramLabel = "TOPIC", description = ["NATS topic to send message to"])
    private var topic: String? = null

    override fun run() {
        if (verbose) {
            println("Running in verbose mode")
        }

        if (topic == null) {
            println("Error: Topic parameter is required")
            return
        }

        // Validate topic
        if (topic != "task.trigger.due") {
            println("Error: Only 'task.trigger.due' topic is currently supported")
            return
        }

        // Send empty message to the topic
        natsService.sendEmptyMessage(topic!!)
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(NetmanTickerCommand::class.java, *args)
        }
    }
}

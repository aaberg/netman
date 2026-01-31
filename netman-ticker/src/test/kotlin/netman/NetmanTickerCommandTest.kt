package netman

import io.micronaut.configuration.picocli.PicocliRunner
import io.micronaut.context.ApplicationContext
import io.micronaut.context.env.Environment
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class NetmanTickerCommandTest {

    @Test
    fun testWithCommandLineOption() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setOut(PrintStream(baos))

        val args = arrayOf("-v", "task.trigger.due")
        PicocliRunner.run(NetmanTickerCommand::class.java, ctx, *args)

        assertThat(baos.toString()).contains("Running in verbose mode")

        ctx.close()
    }

    @Test
    fun testWithInvalidTopic() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setOut(PrintStream(baos))

        val args = arrayOf("invalid.topic")
        PicocliRunner.run(NetmanTickerCommand::class.java, ctx, *args)

        assertThat(baos.toString()).contains("Only 'task.trigger.due' topic is currently supported")

        ctx.close()
    }

    @Test
    fun testWithoutTopic() {
        val ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)
        val baos = ByteArrayOutputStream()
        System.setErr(PrintStream(baos))

        val args = arrayOf<String>()
        PicocliRunner.run(NetmanTickerCommand::class.java, ctx, *args)

        // When no topic is provided, Picocli shows the help message to stderr
        assertThat(baos.toString()).contains("Missing required parameter")

        ctx.close()
    }
}

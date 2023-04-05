package aockt

import io.github.jadarma.aockt.test.AocktDisplayNameExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.engine.test.names.DefaultDisplayNameFormatter

@Suppress("Unused")
object TestConfig : AbstractProjectConfig() {

    override fun extensions() = listOf<Extension>(
        AocktDisplayNameExtension(DefaultDisplayNameFormatter())
    )

    override val parallelism: Int = Runtime.getRuntime().availableProcessors()
}

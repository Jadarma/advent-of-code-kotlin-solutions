package aockt

import io.github.jadarma.aockt.test.AocKtExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

@Suppress("Unused")
object TestConfig : AbstractProjectConfig() {

    override fun extensions() = listOf<Extension>(
        AocKtExtension()
    )

    override val parallelism: Int = Runtime.getRuntime().availableProcessors()
}

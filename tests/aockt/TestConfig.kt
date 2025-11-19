package aockt

import io.github.jadarma.aockt.test.AocKtExtension
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.engine.concurrency.SpecExecutionMode

object TestConfig : AbstractProjectConfig() {

    override val extensions = listOf<Extension>(
        AocKtExtension()
    )

    override val specExecutionMode: SpecExecutionMode = SpecExecutionMode.Concurrent
    override val displayFullTestPath: Boolean = true
}

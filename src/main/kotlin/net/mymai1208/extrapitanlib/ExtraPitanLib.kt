package net.mymai1208.extrapitanlib

import net.fabricmc.api.ModInitializer
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.registry.CompatRegistry
import org.apache.logging.log4j.LogManager

object ExtraPitanLib : ModInitializer {
    val LOGGER = LogManager.getLogger(ExtraPitanLib::class.java)
    val REGISTRY = CompatRegistry.createRegistry("extrapitanlib")

    override fun onInitialize() {
        LOGGER.info("Hello world!")
        val test = ModComponent("extrapitanlib", REGISTRY, null)

        test.createBlock("test_block") {
            settings(CompatibleMaterial.STONE) {
                requiresTool()
            }

            registerBlockItem()
        }

        test.registerAll()
    }
}
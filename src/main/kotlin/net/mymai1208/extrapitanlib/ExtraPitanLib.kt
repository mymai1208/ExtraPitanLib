package net.mymai1208.extrapitanlib

import ml.pkom.mcpitanlibarch.api.registry.ArchRegistry
import net.fabricmc.api.ModInitializer
import net.minecraft.block.Material
import net.mymai1208.extrapitanlib.builder.BlockBuilder
import org.apache.logging.log4j.LogManager

object ExtraPitanLib : ModInitializer {
    val LOGGER = LogManager.getLogger(ExtraPitanLib::class.java)
    val REGISTRY = ArchRegistry.createRegistry("extrapitanlib")

    override fun onInitialize() {
        LOGGER.info("Hello world!")
        val test = ModComponent("extrapitanlib", REGISTRY, null)

    }
}
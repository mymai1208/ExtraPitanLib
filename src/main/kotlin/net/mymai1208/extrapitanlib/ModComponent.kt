package net.mymai1208.extrapitanlib

import ml.pkom.mcpitanlibarch.api.client.registry.ArchRegistryClient
import ml.pkom.mcpitanlibarch.api.registry.ArchRegistry
import net.minecraft.block.Material
import net.mymai1208.extrapitanlib.builder.BlockBuilder

class ModComponent(val modId: String, val REGISTRY: ArchRegistry? = null, val CLIENT_REGISTRY: ArchRegistryClient? = null) {
    private val blocks = mutableListOf<BlockBuilder>()

    fun createBlock(id: String, material: Material, lambda: BlockBuilder.() -> Unit) {
        blocks.add(BlockBuilder(material, modId, id).apply(lambda))
    }

    fun registerBlocks() {
        blocks.forEach {
            REGISTRY?.registerBlock(it.getIdentifier(), it::build)
        }
    }
}
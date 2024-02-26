package net.mymai1208.extrapitanlib

import net.mymai1208.extrapitanlib.builder.BlockBuilder
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient
import net.pitan76.mcpitanlib.api.registry.CompatRegistry

class ModComponent(val modId: String, val REGISTRY: CompatRegistry? = null, val CLIENT_REGISTRY: CompatRegistryClient? = null) {
    private val blocks = mutableListOf<BlockBuilder>()

    fun createBlock(id: String, material: CompatibleMaterial, lambda: BlockBuilder.() -> Unit) {
        blocks.add(BlockBuilder(material, modId, id).apply(lambda))
    }

    fun registerBlocks() {
        blocks.forEach {
            REGISTRY?.registerBlock(it.getIdentifier(), it::build)
        }
    }
}
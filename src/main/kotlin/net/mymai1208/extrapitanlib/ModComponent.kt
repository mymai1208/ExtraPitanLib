package net.mymai1208.extrapitanlib

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.builder.*
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient
import net.pitan76.mcpitanlib.api.registry.CompatRegistry
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder

class ModComponent(val modId: String, val registry: CompatRegistry? = null, val clientRegistry: CompatRegistryClient? = null) {
    internal val builders = mutableListOf<BasicBuilder<out Any>>()

    internal val registeredBlocks = mutableMapOf<Identifier, Block>()
    internal val registeredBlockEntities = mutableMapOf<Identifier, BlockEntityType<*>>()
    internal val registeredItems = mutableMapOf<Identifier, Item>()
    fun createBlock(id: String, lambda: BlockBuilder.() -> Unit): () -> Block? {
        val builder = BlockBuilder(this, id).apply(lambda)
        builders.add(builder)

        return { registeredBlocks[builder.getIdentifier()] }
    }

    fun createBlockEntity(id: String, lambda: BlockEntityBuilder.() -> Unit): () -> BlockEntityType<*>? {
        val builder = BlockEntityBuilder(id, this).apply(lambda)
        builders.add(builder)

        return { registeredBlockEntities[builder.getIdentifier()] }
    }

    private fun registerBlocks() {
        builders.filterIsInstance<BlockBuilder>().forEach {
            val block = registry?.registerBlock(it.getIdentifier(), it::build)?.orNull ?: throw Exception("Failed to register block")
            registeredBlocks[it.getIdentifier()] = block
        }
    }

    private fun registerItems() {
        builders.filterIsInstance<ItemBuilderImpl>().forEach {
            val item = registry?.registerItem(it.getIdentifier(), it::build)?.orNull ?: throw Exception("Failed to register item")
            registeredItems[it.getIdentifier()] = item
        }
    }

    private fun registerBlockItems() {
        builders.filterIsInstance<BlockItemBuilder>().forEach {
            val item = registry?.registerItem(it.getIdentifier(), it::build)?.orNull ?: throw Exception("Failed to register block item")
            registeredItems[it.getIdentifier()] = item
        }
    }

    private fun registerBlockEntities() {
        builders.filterIsInstance<BlockEntityBuilder>().forEach { builder ->
            val blocks = builders
                .filterIsInstance<BlockBuilder>()
                .filter { it.getBlockEntityId() == builder.getIdentifier() }
                .mapNotNull { registeredBlocks[it.getIdentifier()] }

            val blockEntity = registry?.registerBlockEntityType(builder.getIdentifier()) {
                BlockEntityTypeBuilder.create(
                    { builder.build(registeredBlockEntities[builder.getIdentifier()]!!, it) },
                    *blocks.toTypedArray()
                ).build()
            }?.orNull ?: throw Exception("Failed to register block entity")

            registeredBlockEntities[builder.getIdentifier()] = blockEntity
        }
    }

    fun registerAll() {
        registerItems()
        registerBlocks()
        registerBlockItems()
        registerBlockEntities()
    }
}
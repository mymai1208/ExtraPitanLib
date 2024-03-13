package net.mymai1208.extrapitanlib

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.builder.BasicBuilder
import net.mymai1208.extrapitanlib.builder.impl.BlockBuilder
import net.mymai1208.extrapitanlib.builder.impl.BlockEntityBuilder
import net.mymai1208.extrapitanlib.builder.impl.BlockItemBuilder
import net.mymai1208.extrapitanlib.builder.impl.ItemBuilderImpl
import net.pitan76.mcpitanlib.api.registry.CompatRegistry
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder

class ModComponent(val modId: String, val registry: CompatRegistry? = null) {
    internal val builders = mutableListOf<BasicBuilder<out Any>>()

    internal val registeredBlocks = mutableMapOf<Identifier, Block>()
    internal val registeredBlockEntities = mutableMapOf<Identifier, BlockEntityType<*>>()
    internal val registeredItems = mutableMapOf<Identifier, Item>()

    fun createBlock(id: String, lambda: BlockBuilder.() -> Unit): () -> Block {
        if(builders.any { it.getIdentifier().path == id }) {
            throw Exception("Block with id $id already exists")
        }

        val builder = BlockBuilder(this, id).apply(lambda)
        builders.add(builder)

        return { registeredBlocks[builder.getIdentifier()] ?: throw Exception("Block with id $id not found") }
    }

    fun createBlockEntity(id: String, lambda: BlockEntityBuilder.() -> Unit): () -> BlockEntityType<*> {
        val builder = BlockEntityBuilder(id, this).apply(lambda)
        builders.add(builder)

        return { registeredBlockEntities[builder.getIdentifier()] ?: throw Exception("BlockEntity with id $id not found") }
    }

    fun createItem(id: String, lambda: ItemBuilderImpl.() -> Unit): () -> Item {
        if(builders.any { it.getIdentifier().path == id }) {
            throw Exception("Item with id $id already exists")
        }

        val builder = ItemBuilderImpl(this, id).apply(lambda)
        builders.add(builder)

        return { registeredItems[builder.getIdentifier()] ?: throw Exception("Item with id $id not found") }
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
                .map { registeredBlocks[it.getIdentifier()]!! }

            val blockEntity = registry?.registerBlockEntityType(builder.getIdentifier()) {
                BlockEntityTypeBuilder.create(
                    { builder.build(registeredBlockEntities[builder.getIdentifier()]!!, it) },
                    *blocks.toTypedArray()
                ).build()
            }?.orNull ?: throw Exception("Failed to register block entity")

            registeredBlockEntities[builder.getIdentifier()] = blockEntity
        }
    }

    @Environment(EnvType.CLIENT)
    private fun registerRenderLayers() {
        val blocks = builders.filterIsInstance<BlockBuilder>().filter { it.renderLayerMap != null }

        blocks.forEach {
            val blockInstance = registeredBlocks[it.getIdentifier()]!!

            BlockRenderLayerMap.INSTANCE.putBlock(blockInstance, it.renderLayerMap)
        }
    }

    fun registerAll() {
        if(registry == null) {
            throw Exception("Registry is not initialized")
        }

        registerItems()
        registerBlocks()
        registerBlockItems()
        registerBlockEntities()
    }

    @Environment(EnvType.CLIENT)
    fun registerAllClient() {
        registerRenderLayers()
    }
}
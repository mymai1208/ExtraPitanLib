package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.builder.BasicBuilder
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.block.ExtendBlock
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider

class BlockBuilder(val modComponent: ModComponent, id: String) : BasicBuilder<ExtendBlock> {
    private var settings: CompatibleBlockSettings = CompatibleBlockSettings.of(CompatibleMaterial.STONE)
    private var blockEntityId: Identifier? = null

    private var isUseBlockEntity: Boolean = false
    private var isTick: Boolean = false

    internal var renderLayerMap: RenderLayer? = null

    private val id: Identifier = Identifier(modComponent.modId, id)

    fun settings(lambda: CompatibleBlockSettings.() -> Unit): BlockBuilder {
        settings = settings.apply(lambda)

        return this
    }

    fun settings(material: CompatibleMaterial, lambda: CompatibleBlockSettings.() -> Unit): BlockBuilder {
        settings = CompatibleBlockSettings.of(material).apply(lambda)

        return this
    }

    fun blockEntity(id: String? = null, isUseTick: Boolean, lambda: BlockEntityBuilder.() -> Unit): BlockBuilder {
        blockEntityId = Identifier(modComponent.modId, id ?: this.id.path)

        isTick = isUseTick
        isUseBlockEntity = true

        modComponent.createBlockEntity(id ?: this.id.path, lambda)

        return this
    }

    fun registerBlockItem(blockItemBuilder: (BlockItemBuilder.() -> Unit)? = null): BlockBuilder {
        if(blockItemBuilder == null) {
            modComponent.builders.add(BlockItemBuilder(modComponent, id))
            return this
        }

        modComponent.builders.add(BlockItemBuilder(modComponent, id).apply(blockItemBuilder))

        return this
    }

    fun setRenderLayerMap(renderLayer: RenderLayer): BlockBuilder {
        renderLayerMap = renderLayer

        return this
    }

    override fun build(): ExtendBlock {
        if(blockEntityId == null || !isUseBlockEntity) {
            return ExtendBlock(settings)
        }

        return object : ExtendBlock(settings), ExtendBlockEntityProvider {
            override fun <T : BlockEntity> getBlockEntityType(): BlockEntityType<T> {
                return modComponent.registeredBlockEntities[blockEntityId] as? BlockEntityType<T> ?: throw Exception("BlockEntity not found")
            }

            override fun isTick(): Boolean {
                return this@BlockBuilder.isTick
            }
        }
    }

    override fun getIdentifier(): Identifier {
        return id
    }

    fun getBlockEntityId(): Identifier? {
        return blockEntityId
    }
}
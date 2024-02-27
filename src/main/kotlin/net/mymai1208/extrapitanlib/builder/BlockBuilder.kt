package net.mymai1208.extrapitanlib.builder

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.block.ExtendBlock
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider

class BlockBuilder(val modComponent: ModComponent, id: String) : BasicBuilder<ExtendBlock> {
    private var settings: CompatibleBlockSettings = CompatibleBlockSettings.of(CompatibleMaterial.STONE)
    private var blockEntityId: Identifier? = null
    private var isTick: Boolean = false
    private val id: Identifier = Identifier(modComponent.modId, id)

    fun settings(lambda: CompatibleBlockSettings.() -> Unit): BlockBuilder {
        settings = settings.apply(lambda)

        return this
    }

    fun settings(material: CompatibleMaterial, lambda: CompatibleBlockSettings.() -> Unit): BlockBuilder {
        settings = CompatibleBlockSettings.of(material).apply(lambda)

        return this
    }

    fun useTick(): BlockBuilder {
        isTick = true

        return this
    }

    fun blockEntity(id: String, lambda: BlockEntityBuilder.() -> Unit): BlockBuilder {
        blockEntityId = Identifier(modComponent.modId, id)
        modComponent.createBlockEntity(id, lambda)

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

    override fun build(): ExtendBlock {
        if(blockEntityId == null) {
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
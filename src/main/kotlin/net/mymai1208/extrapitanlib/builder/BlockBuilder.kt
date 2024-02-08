package net.mymai1208.extrapitanlib.builder

import ml.pkom.mcpitanlibarch.api.block.ExtendBlockEntityProvider
import net.minecraft.block.AbstractBlock.Settings
import net.minecraft.block.Block
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.Material
import net.minecraft.util.Identifier

class BlockBuilder(material: Material, modId: String, id: String) : BasicBuilder {
    private var settings = Settings.of(material)
    private var blockEntity: ExtendBlockEntityProvider? = null
    private val id: Identifier = Identifier(modId, id)

    fun settings(lambda: Settings.() -> Unit): BlockBuilder {
        settings.apply(lambda)

        return this
    }

    fun blockEntity(lambda: ExtendBlockEntityProvider.() -> Unit): BlockBuilder {
        blockEntity = object : ExtendBlockEntityProvider { }.apply(lambda)

        return this
    }

    fun build(): Block {
        return if(blockEntity == null) {
            Block(settings)
        } else {
            object : Block(settings), ExtendBlockEntityProvider {

            }
        }
    }

    override fun getIdentifier(): Identifier {
        return id
    }
}
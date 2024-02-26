package net.mymai1208.extrapitanlib.builder

import net.minecraft.util.Identifier
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.block.ExtendBlock
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider

class BlockBuilder(material: CompatibleMaterial, modId: String, id: String) : BasicBuilder {
    private var settings = CompatibleBlockSettings.of(material)
    private var blockEntity: ExtendBlockEntityProvider? = null
    private val id: Identifier = Identifier(modId, id)

    fun settings(lambda: CompatibleBlockSettings.() -> Unit): BlockBuilder {
        settings.apply(lambda)

        return this
    }

    fun blockEntity(lambda: ExtendBlockEntityProvider.() -> Unit): BlockBuilder {
        blockEntity = object : ExtendBlockEntityProvider { }.apply(lambda)

        return this
    }

    fun build(): ExtendBlock {
        return if(blockEntity == null) {
            ExtendBlock(settings)
        } else {
            object : ExtendBlock(settings), ExtendBlockEntityProvider {

            }
        }
    }

    override fun getIdentifier(): Identifier {
        return id
    }
}
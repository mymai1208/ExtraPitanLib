package net.mymai1208.extrapitanlib.builder

import net.minecraft.block.entity.BlockEntityType
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity

interface BlockEntityBuilder : BasicBuilder<ExtendBlockEntity> {
    fun build(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity

    override fun build(): ExtendBlockEntity {
        throw UnsupportedOperationException("This method is not supported for BlockEntityBuilder")
    }
}
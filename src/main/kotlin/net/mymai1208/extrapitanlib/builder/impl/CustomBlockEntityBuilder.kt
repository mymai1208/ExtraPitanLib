package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.builder.BlockEntityBuilder
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity

class CustomBlockEntityBuilder<T : ExtendBlockEntity>(val modComponent: ModComponent, val id: String, val lambda: (blockEntityType: BlockEntityType<*>, event: TileCreateEvent) -> T) : BlockEntityBuilder {
    private val identifier = Identifier(modComponent.modId, id)

    override fun build(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        return lambda(blockEntityType, event)
    }

    override fun getIdentifier(): Identifier {
        return identifier
    }
}
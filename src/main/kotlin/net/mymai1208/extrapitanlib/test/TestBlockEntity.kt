package net.mymai1208.extrapitanlib.test

import net.minecraft.block.entity.BlockEntityType
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker

class TestBlockEntity(val blockEntityType: BlockEntityType<*>, val tileEvent: TileCreateEvent) : ExtendBlockEntity(blockEntityType, tileEvent), ExtendBlockEntityTicker<TestBlockEntity> {
    override fun tick(event: TileTickEvent<TestBlockEntity>) {
        println("Tick")
    }
}
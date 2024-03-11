package net.mymai1208.extrapitanlib.test

import net.minecraft.block.entity.BlockEntityType
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker

class TestBlockEntity(type: BlockEntityType<*>, val event: TileCreateEvent) : ExtendBlockEntity(type, event),
    ExtendBlockEntityTicker<TestBlockEntity> {
    override fun tick(event: TileTickEvent<TestBlockEntity>?) {
        println("Tick")
    }

}
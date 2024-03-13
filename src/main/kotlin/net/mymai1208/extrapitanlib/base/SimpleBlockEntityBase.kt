package net.mymai1208.extrapitanlib.base

import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.mymai1208.extrapitanlib.builder.impl.BlockEntityBuilder
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity

internal open class SimpleBlockEntityBase(val builder: BlockEntityBuilder, blockEntityType: BlockEntityType<*>, event: TileCreateEvent) : ExtendBlockEntity(blockEntityType, event) {
    internal var readNbtLambda: (BlockEntityBuilder.(NbtCompound) -> Unit)? = null
    internal var writeNbtLambda: (BlockEntityBuilder.(NbtCompound) -> Unit)? = null

    override fun writeNbtOverride(nbt: NbtCompound) {
        super.writeNbtOverride(nbt)

        writeNbtLambda?.let { it(builder, nbt) }
    }

    override fun readNbtOverride(nbt: NbtCompound?) {
        super.readNbtOverride(nbt)

        readNbtLambda?.let { it(builder, nbt!!) }
    }
}
package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent

class BlockEntityImplBuilder {
    internal var readNbtLambda: (BlockEntityBuilder.(NbtCompound) -> Unit)? = null
    internal var writeNbtLambda: (BlockEntityBuilder.(NbtCompound) -> Unit)? = null
    internal var tickLambda: ((TileTickEvent<BlockEntity>) -> Unit)? = null

    fun readNbt(lambda: BlockEntityBuilder.(NbtCompound) -> Unit): BlockEntityImplBuilder {
        readNbtLambda = lambda

        return this
    }

    fun writeNbt(lambda: BlockEntityBuilder.(NbtCompound) -> Unit): BlockEntityImplBuilder {
        writeNbtLambda = lambda

        return this
    }

    fun onTick(lambda: (TileTickEvent<BlockEntity>) -> Unit): BlockEntityImplBuilder {
        tickLambda = lambda

        return this
    }
}
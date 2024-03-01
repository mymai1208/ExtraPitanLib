package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.base.BlockEntityBase
import net.mymai1208.extrapitanlib.builder.BasicBuilder
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker

class BlockEntityBuilder(val id: String, val modComponent: ModComponent) : BasicBuilder<ExtendBlockEntity> {
    private val identifier = Identifier(modComponent.modId, id)
    private var readNbtLambda: (BlockEntityBuilder.(NbtCompound) -> Unit)? = null
    private var writeNbtLambda: (BlockEntityBuilder.(NbtCompound) -> Unit)? = null
    private var tickLambda: ((world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) -> Unit)? = null

    fun build(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        if(tickLambda != null) {
            return createBlockEntityWithTicker(blockEntityType, event)
        }

        return createBlockEntity(blockEntityType, event)
    }

    fun readNbt(lambda: BlockEntityBuilder.(NbtCompound) -> Unit): BlockEntityBuilder {
        readNbtLambda = lambda

        return this
    }

    fun writeNbt(lambda: BlockEntityBuilder.(NbtCompound) -> Unit): BlockEntityBuilder {
        writeNbtLambda = lambda

        return this
    }

    fun onTick(lambda: (world: World, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) -> Unit): BlockEntityBuilder {
        tickLambda = lambda

        return this
    }

    private fun createBlockEntity(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        return BlockEntityBase(this, blockEntityType, event).apply {
            readNbtLambda = this@BlockEntityBuilder.readNbtLambda
            writeNbtLambda = this@BlockEntityBuilder.writeNbtLambda
        }
    }

    private fun createBlockEntityWithTicker(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        return object : BlockEntityBase(this, blockEntityType, event), ExtendBlockEntityTicker<BlockEntity> {
            override fun tick(event: TileTickEvent<BlockEntity>) {
                tickLambda?.let { it(event.world, event.pos, event.state, event.blockEntity) }
            }
        }.apply {
            readNbtLambda = this@BlockEntityBuilder.readNbtLambda
            writeNbtLambda = this@BlockEntityBuilder.writeNbtLambda
        }
    }

    override fun getIdentifier(): Identifier {
        return identifier
    }

    override fun build(): ExtendBlockEntity {
        throw Exception("BlockEntityBuilder cannot be built without TileCreateEvent")
    }
}
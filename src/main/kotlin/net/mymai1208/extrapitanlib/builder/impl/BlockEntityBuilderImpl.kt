package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.base.SimpleBlockEntityBase
import net.mymai1208.extrapitanlib.builder.BlockEntityBuilder
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker

class BlockEntityBuilderImpl(val id: String, val modComponent: ModComponent) : BlockEntityBuilder {
    private val identifier = Identifier(modComponent.modId, id)
    private var readNbtLambda: (BlockEntityBuilderImpl.(NbtCompound) -> Unit)? = null
    private var writeNbtLambda: (BlockEntityBuilderImpl.(NbtCompound) -> Unit)? = null
    private var tickLambda: ((TileTickEvent<BlockEntity>) -> Unit)? = null

    override fun build(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        if(tickLambda != null) {
            return createBlockEntityWithTicker(blockEntityType, event)
        }

        return createBlockEntity(blockEntityType, event)
    }

    fun readNbt(lambda: BlockEntityBuilderImpl.(NbtCompound) -> Unit): BlockEntityBuilderImpl {
        readNbtLambda = lambda

        return this
    }

    fun writeNbt(lambda: BlockEntityBuilderImpl.(NbtCompound) -> Unit): BlockEntityBuilderImpl {
        writeNbtLambda = lambda

        return this
    }

    fun onTick(lambda: (TileTickEvent<BlockEntity>) -> Unit): BlockEntityBuilderImpl {
        tickLambda = lambda

        return this
    }

    private fun createBlockEntity(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        return SimpleBlockEntityBase(this, blockEntityType, event).apply {
            readNbtLambda = this@BlockEntityBuilderImpl.readNbtLambda
            writeNbtLambda = this@BlockEntityBuilderImpl.writeNbtLambda
        }
    }

    private fun createBlockEntityWithTicker(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        return object : SimpleBlockEntityBase(this, blockEntityType, event), ExtendBlockEntityTicker<BlockEntity> {
            override fun tick(event: TileTickEvent<BlockEntity>) {
                tickLambda?.let { it(event) }
            }
        }.apply {
            readNbtLambda = this@BlockEntityBuilderImpl.readNbtLambda
            writeNbtLambda = this@BlockEntityBuilderImpl.writeNbtLambda
        }
    }

    override fun getIdentifier(): Identifier {
        return identifier
    }
}
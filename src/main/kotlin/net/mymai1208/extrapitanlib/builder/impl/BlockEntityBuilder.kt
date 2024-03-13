package net.mymai1208.extrapitanlib.builder.impl

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.mymai1208.extrapitanlib.ModComponent
import net.mymai1208.extrapitanlib.base.SimpleBlockEntityBase
import net.mymai1208.extrapitanlib.builder.BasicBuilder
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker

class BlockEntityBuilder(val id: String, val isTick: Boolean, val modComponent: ModComponent) : BasicBuilder<ExtendBlockEntity> {
    private val identifier = Identifier(modComponent.modId, id)

    private var customBlockEntity: ((blockEntityType: BlockEntityType<*>, event: TileCreateEvent) -> ExtendBlockEntity)? = null
    private var blockEntityImplBuilder: BlockEntityImplBuilder? = null

    internal var blockEntityRenderer: BlockEntityRenderer<out BlockEntity>? = null

    fun build(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        if(customBlockEntity != null) {
            return customBlockEntity!!(blockEntityType, event)
        }

        if(isTick) {
            return createBlockEntityWithTicker(blockEntityType, event)
        }

        return createBlockEntity(blockEntityType, event)
    }

    fun <T : ExtendBlockEntity> custom(lambda: (blockEntityType: BlockEntityType<*>, event: TileCreateEvent) -> T): BlockEntityBuilder {
        if(blockEntityImplBuilder != null) {
            throw Exception("BlockEntity is already set")
        }

        customBlockEntity = lambda

        return this
    }

    fun implementation(builder: BlockEntityImplBuilder.() -> Unit): BlockEntityBuilder {
        if(customBlockEntity != null) {
            throw Exception("BlockEntity is already set")
        }

        blockEntityImplBuilder = BlockEntityImplBuilder().apply(builder)

        return this
    }

    fun renderer(lambda: (blockEntity: BlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) -> Unit): BlockEntityBuilder {
        if(blockEntityRenderer != null) {
            throw Exception("BlockEntityRenderer is already set")
        }

        blockEntityRenderer = BlockEntityRenderer<BlockEntity> { blockEntity, f, matrixStack, vertexConsumerProvider, i, j -> lambda(blockEntity!!, f, matrixStack!!, vertexConsumerProvider!!, i, j) }

        return this
    }

    fun <T : BlockEntity> renderer(renderer: BlockEntityRenderer<out T>): BlockEntityBuilder {
        if(blockEntityRenderer != null) {
            throw Exception("BlockEntityRenderer is already set")
        }

        blockEntityRenderer = renderer

        return this
    }

    private fun createBlockEntity(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        if(blockEntityImplBuilder == null) {
            throw Exception("BlockEntityImplBuilder is not set")
        }

        return SimpleBlockEntityBase(this, blockEntityType, event).apply {
            readNbtLambda = blockEntityImplBuilder!!.readNbtLambda
            writeNbtLambda = blockEntityImplBuilder!!.writeNbtLambda
        }
    }

    private fun createBlockEntityWithTicker(blockEntityType: BlockEntityType<*>, event: TileCreateEvent): ExtendBlockEntity {
        return object : SimpleBlockEntityBase(this, blockEntityType, event), ExtendBlockEntityTicker<BlockEntity> {
            override fun tick(event: TileTickEvent<BlockEntity>) {
                blockEntityImplBuilder!!.tickLambda?.let { it(event) }
            }
        }.apply {
            readNbtLambda = blockEntityImplBuilder!!.readNbtLambda
            writeNbtLambda = blockEntityImplBuilder!!.writeNbtLambda
        }
    }

    override fun getIdentifier(): Identifier {
        return identifier
    }

    override fun build(): ExtendBlockEntity {
        throw Exception("BlockEntityBuilder cannot be built")
    }
}
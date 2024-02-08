package net.mymai1208.extrapitanlib.network

import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.minecraft.network.PacketByteBuf
import kotlin.reflect.KClass

enum class PacketType(val type: KClass<*>, val function: (PacketByteBuf) -> Any) {
    Int(kotlin.Int::class, PacketByteUtil::readInt),
    String(kotlin.String::class, PacketByteUtil::readString),
    Boolean(kotlin.Boolean::class, PacketByteUtil::readBoolean),
    Float(kotlin.Float::class, PacketByteUtil::readFloat),
    Double(kotlin.Double::class, PacketByteUtil::readDouble),
    Long(kotlin.Long::class, PacketByteUtil::readLong),
    Short(kotlin.Short::class, PacketByteUtil::readShort),
    Byte(kotlin.Byte::class, PacketByteUtil::readByte),
    BlockPos(net.minecraft.util.math.BlockPos::class, PacketByteUtil::readBlockPos),
    UUID(java.util.UUID::class, PacketByteUtil::readUuid),
    Identifier(net.minecraft.util.Identifier::class, PacketByteUtil::readIdentifier),
    ItemStack(net.minecraft.item.ItemStack::class, PacketByteUtil::readItemStack)
}
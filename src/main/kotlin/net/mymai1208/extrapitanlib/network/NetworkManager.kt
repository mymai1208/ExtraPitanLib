package net.mymai1208.extrapitanlib.network

import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.mymai1208.extrapitanlib.ExtraPitanLib
import net.mymai1208.extrapitanlib.network.annotation.NetworkingPacket
import net.mymai1208.extrapitanlib.network.annotation.UnlimitedNBT
import net.mymai1208.extrapitanlib.network.annotation.VariableValue
import org.jetbrains.annotations.Nullable
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

object NetworkManager {
    val READ_FUNCTIONS = mapOf<KClass<*>, (PacketByteBuf) -> Any>(
        Int::class to PacketByteUtil::readInt,
        String::class to PacketByteUtil::readString,
        Boolean::class to PacketByteUtil::readBoolean,
        Float::class to PacketByteUtil::readFloat,
        Double::class to PacketByteUtil::readDouble,
        Long::class to PacketByteUtil::readLong,
        Short::class to PacketByteUtil::readShort,
        Byte::class to PacketByteUtil::readByte,
        BlockPos::class to PacketByteUtil::readBlockPos,
        UUID::class to PacketByteUtil::readUuid,
        Identifier::class to PacketByteUtil::readIdentifier,
        ItemStack::class to PacketByteUtil::readItemStack,
        ByteArray::class to PacketByteUtil::readByteArray,
        IntArray::class to PacketByteUtil::readIntArray,
        LongArray::class to PacketByteUtil::readLongArray
    )

    fun <T : Any> parse(targetClazz: KClass<T>, buffer: PacketByteBuf): T? {
        if(!targetClazz.hasAnnotation<NetworkingPacket>()) {
            return null
        }

        if(targetClazz.primaryConstructor == null) {
            return null
        }

        val constructor = targetClazz.primaryConstructor!!

        val constructorParams: MutableList<Any> = mutableListOf()

        for (parameter in constructor.parameters) {
            val returnType = parameter.type
            val returnClazz = returnType.classifier as? KClass<*> ?: continue

            if(!returnType.isMarkedNullable && !parameter.hasAnnotation<Nullable>()) {
                ExtraPitanLib.LOGGER.warn("${parameter.name} is not nullable")
                continue
            }

            //データが複雑じゃなければ次のパラメータに
            val simpleValue = this.readValue(parameter, buffer)
            if(simpleValue != null) {
                constructorParams.add(simpleValue)

                continue
            }

            if(returnClazz.isSubclassOf(NbtElement::class)) {
                if(parameter.hasAnnotation<UnlimitedNBT>()) {
                    constructorParams.add(PacketByteUtil.readUnlimitedNbt(buffer))
                }
                else {
                    constructorParams.add(PacketByteUtil.readNbt(buffer))
                }

                continue
            }

            if(returnClazz.isSubclassOf(Map::class)) {
                val keyClazz = returnType.arguments[0].type?.classifier as? KClass<*> ?: continue
                val valueClazz = returnType.arguments[1].type?.classifier as? KClass<*> ?: continue

                if(keyClazz != String::class) {
                    continue
                }

                //Valueの型がパケットに使用出来るか確認
                if(!READ_FUNCTIONS.containsKey(valueClazz)) {
                    continue
                }

                val map = PacketByteUtil.readMap(buffer, READ_FUNCTIONS[keyClazz], READ_FUNCTIONS[valueClazz]) ?: continue

                constructorParams.add(map)
            }
        }

        val instance = constructor.call(*constructorParams.toTypedArray())

        return instance
    }

    inline fun <reified T : Any> parse(buffer: PacketByteBuf): T? {
        return parse(T::class, buffer)
    }

    private fun readValue(parameter: KParameter, buffer: PacketByteBuf): Any? {
        //Check Serialized
        if(parameter.hasAnnotation<VariableValue>()) {
            return when(parameter.type.classifier) {
                Int::class -> PacketByteUtil.readVarInt(buffer)
                Long::class -> PacketByteUtil.readVarLong(buffer)
                else -> null
            }
        }

        val clazz = parameter.type.classifier as? KClass<*> ?: return null

        return readValue(clazz, buffer)
    }

    private fun readValue(clazz: KClass<*>, buffer: PacketByteBuf): Any? {
        try {
            return READ_FUNCTIONS[clazz]?.let { it(buffer) }
        } catch (e: Exception) {
            ExtraPitanLib.LOGGER.warn("Failed to read value of type ${clazz.simpleName}")
        }

        return null
    }
}
package net.mymai1208.extrapitanlib.network

import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.mymai1208.extrapitanlib.ExtraPitanLib
import net.mymai1208.extrapitanlib.network.annotation.NetworkingPacket
import net.mymai1208.extrapitanlib.network.annotation.UnlimitedNBT
import net.mymai1208.extrapitanlib.network.annotation.VariantValue
import java.util.UUID
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

object Parser {
    inline fun <reified T : Any> parse(buffer: PacketByteBuf): T? {
        if(!T::class.hasAnnotation<NetworkingPacket>()) {
            return null
        }

        if(!T::class.isData) {
            return null
        }

        if(T::class.primaryConstructor == null) {
            return null
        }

        val constructor = T::class.primaryConstructor!!

        val constructorParams: MutableList<Any> = mutableListOf()

        for (parameter in constructor.parameters) {
            if(parameter !is KMutableProperty<*>) {
                continue
            }

            val returnType = parameter.returnType

            //データが複雑じゃなければ次のパラメータに
            val simpleValue = readValue(parameter, buffer)
            if(simpleValue != null) {
                constructorParams.add(simpleValue)

                continue
            }

            //Check Unlimited NBT
            if(parameter.hasAnnotation<UnlimitedNBT>() && returnType.isSupertypeOf(NbtElement::class.starProjectedType)) {
                constructorParams.add(PacketByteUtil.readUnlimitedNbt(buffer))

                continue
            }

            if(returnType.isSubtypeOf(Map::class.starProjectedType)) {
                val keyClazz = returnType.arguments[0].type?.classifier ?: continue
                val valueClazz = returnType.arguments[1].type?.classifier ?: continue

                if(keyClazz != String::class) {
                    continue
                }

                if(PacketType.entries.find { it.type == valueClazz } == null) {
                    continue
                }

                val value = readValue(valueClazz as? KClass<*>, buffer) ?: continue

                constructorParams.add(value)
            }

            if(!returnType.isMarkedNullable) {
                ExtraPitanLib.LOGGER.warn("${parameter.name} is not nullable")
                continue
            }
        }

        val instance = constructor.call(*constructorParams.toTypedArray())

        return instance
    }

    fun readValue(property: KProperty<*>, buffer: PacketByteBuf): Any? {
        //Check Serialized
        if(property.hasAnnotation<VariantValue>()) {
            return when(property.returnType.classifier) {
                Int::class -> PacketByteUtil.readVarInt(buffer)
                Long::class -> PacketByteUtil.readVarLong(buffer)
                else -> null
            }
        }

        return readValue(property.returnType.classifier as? KClass<*>, buffer)
    }

    fun readValue(clazz: KClass<*>?, buffer: PacketByteBuf): Any? {
        return PacketType.entries.find { it.type == clazz }?.let { it.function(buffer) }
    }
}
package net.mymai1208.extrapitanlib.network

import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf
import net.mymai1208.extrapitanlib.ExtraPitanLib
import net.mymai1208.extrapitanlib.network.annotation.NetworkingPacket
import net.mymai1208.extrapitanlib.network.annotation.UnlimitedNBT
import net.mymai1208.extrapitanlib.network.annotation.VariableValue
import kotlin.reflect.*
import kotlin.reflect.full.*

object Parser {
    inline fun <reified T : Any> parse(buffer: PacketByteBuf): T? {
        if(!T::class.hasAnnotation<NetworkingPacket>()) {
            return null
        }

        if(T::class.primaryConstructor == null) {
            return null
        }

        val constructor = T::class.primaryConstructor!!

        val constructorParams: MutableList<Any> = mutableListOf()

        for (parameter in constructor.parameters) {
            val returnType = parameter.type

            //データが複雑じゃなければ次のパラメータに
            val simpleValue = readValue(parameter, buffer)
            if(simpleValue != null) {
                constructorParams.add(simpleValue)

                continue
            }

            //Check Unlimited NBT
            if(parameter.hasAnnotation<UnlimitedNBT>() && returnType.isSubtypeOf(NbtElement::class.starProjectedType)) {
                constructorParams.add(PacketByteUtil.readUnlimitedNbt(buffer))

                continue
            }

            if(returnType.isSubtypeOf(Map::class.starProjectedType)) {
                val keyClazz = returnType.arguments[0].type?.classifier as? KClass<*> ?: continue
                val valueClazz = returnType.arguments[1].type?.classifier as? KClass<*> ?: continue

                if(keyClazz != String::class) {
                    continue
                }

                if(PacketType.entries.none { it.type == valueClazz }) {
                    continue
                }

                val value = readValue(keyClazz, buffer) ?: continue

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

    fun readValue(parameter: KParameter, buffer: PacketByteBuf): Any? {
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

    fun readValue(clazz: KClass<*>, buffer: PacketByteBuf): Any? {
        try {
            return PacketType.entries.find { it.type == clazz }?.let { it.function(buffer) }
        } catch (e: Exception) {
            ExtraPitanLib.LOGGER.warn("Failed to read value of type ${clazz.simpleName}")
        }

        return null
    }
}
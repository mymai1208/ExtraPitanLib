package net.mymai1208.extrapitanlib.network

import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.minecraft.nbt.NbtElement
import net.minecraft.network.PacketByteBuf
import net.mymai1208.extrapitanlib.ExtraPitanLib
import net.mymai1208.extrapitanlib.network.annotation.NetworkingPacket
import net.mymai1208.extrapitanlib.network.annotation.UnlimitedNBT
import net.mymai1208.extrapitanlib.network.annotation.VariableValue
import org.jetbrains.annotations.Nullable
import kotlin.reflect.*
import kotlin.reflect.full.*

object NetworkManager {
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
                val valueType = PacketType.entries.find { it.type == valueClazz } ?: continue

                val map = PacketByteUtil.readMap(buffer, PacketType.String.function, valueType.function) ?: continue

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
            return PacketType.entries.find { it.type == clazz }?.let { it.function(buffer) }
        } catch (e: Exception) {
            ExtraPitanLib.LOGGER.warn("Failed to read value of type ${clazz.simpleName}")
        }

        return null
    }
}
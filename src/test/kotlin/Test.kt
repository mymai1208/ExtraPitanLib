import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.minecraft.nbt.NbtCompound
import net.mymai1208.extrapitanlib.network.NetworkManager

fun main() {
    val packet = PacketByteUtil.create()
    packet.writeMap(mapOf("test" to "aaa"), PacketByteUtil::writeString, PacketByteUtil::writeString)

    println(NetworkManager.parse<TestPacket>(packet))
}
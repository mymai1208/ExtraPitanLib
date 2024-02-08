import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.minecraft.nbt.NbtCompound
import net.mymai1208.extrapitanlib.network.NetworkManager

fun main() {
    val packet = PacketByteUtil.create().apply {
        writeMap(mapOf("test" to "aaa"), PacketByteUtil::writeString, PacketByteUtil::writeString)
        writeNbt(NbtCompound().apply {
            putInt("house", 2023)
        })
        writeString("kakauzya")
        writeInt(30)
    }

    println(NetworkManager.parse<TestPacket>(packet))
}
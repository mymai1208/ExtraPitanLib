import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil
import net.mymai1208.extrapitanlib.network.Parser

fun main() {
    val packet = PacketByteUtil.create()
    packet.writeVarInt(1)

    println(Parser.parse<TestPacket>(packet))
}
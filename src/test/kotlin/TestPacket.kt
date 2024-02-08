import net.mymai1208.extrapitanlib.network.annotation.NetworkingPacket
import net.mymai1208.extrapitanlib.network.annotation.VariableValue

@NetworkingPacket
data class TestPacket(@VariableValue var test: Int)
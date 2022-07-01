package zimnycat.utilrun.events;

import net.minecraft.network.Packet;
import zimnycat.utilrun.base.EventBase;

public class SendPacketEvent extends EventBase {
    private final Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) { this.packet = packet; }

    public Packet<?> getPacket() { return packet; }
}

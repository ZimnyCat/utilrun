package zimnycat.utilrun.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zimnycat.utilrun.Utilrun;
import zimnycat.utilrun.events.ReadPacketEvent;
import zimnycat.utilrun.events.SendPacketEvent;

@Mixin(ClientConnection.class)
public class MClientConnection {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, CallbackInfo ci) {
        SendPacketEvent event = new SendPacketEvent(packet);
        Utilrun.bus.post(event);
        if (event.isCancelled()) ci.cancel();
    }

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext_1, Packet<?> packet_1, CallbackInfo ci) {
        ReadPacketEvent event = new ReadPacketEvent(packet_1);
        Utilrun.bus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}

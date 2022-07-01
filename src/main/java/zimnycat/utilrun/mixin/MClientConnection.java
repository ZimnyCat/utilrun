package zimnycat.utilrun.mixin;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zimnycat.utilrun.base.Utilrun;
import zimnycat.utilrun.events.SendPacketEvent;

@Mixin(ClientConnection.class)
public class MClientConnection {
    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"), cancellable = true)
    private void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> packetCallback, CallbackInfo ci) {
        SendPacketEvent event = new SendPacketEvent(packet);
        Utilrun.bus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}

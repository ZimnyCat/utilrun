package zimnycat.utilrun.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zimnycat.utilrun.Utilrun;
import zimnycat.utilrun.events.KeyPressEvent;

@Mixin(Keyboard.class)
public class MKeyboard {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        KeyPressEvent event = new KeyPressEvent(key);
        Utilrun.bus.post(event);
        if (event.isCancelled()) ci.cancel();
    }
}

package net.itzplugins.fullscreenplus.mixin;

import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardHandlerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;toggleFullscreen()V"), cancellable = true)
    public void redirect$handleFullScreenToggle(CallbackInfo ci) {
        FullscreenPlus.toggleFullScreenMode(client.options, !client.options.getFullscreen().getValue());
        client.options.write();
        ci.cancel();
    }

}

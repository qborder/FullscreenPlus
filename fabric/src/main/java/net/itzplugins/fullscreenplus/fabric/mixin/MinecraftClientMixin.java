package net.itzplugins.fullscreenplus.fabric.mixin;

import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InactivityFpsLimiter.class)
public class MinecraftClientMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "update", at = @At("RETURN"), cancellable = true)
    private void fullscreenplus$limitUnfocusedFps(CallbackInfoReturnable<Integer> cir) {
        if (!client.isWindowFocused()) {
            FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();
            if (config.unfocusedFpsLimit) {
                int limit = Math.min(cir.getReturnValue(), config.unfocusedFpsMax);
                cir.setReturnValue(limit);
            }
        }
    }
}

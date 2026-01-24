package net.itzplugins.fullscreenplus.fabric.mixin;

import net.minecraft.client.util.Window;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.fabric.FullscreenPlusFabricCaching;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Borderless implementation
 *
 * @author DeeChael
 */
@Mixin(value = Window.class, priority = 2000)
public abstract class VulkanWindowMixin {

    @Shadow
    private boolean fullscreen;

    @Inject(method = "onWindowPosChanged", at = @At("HEAD"))
    private void inject$onWindowPosChanged$head(long window, int x, int y, CallbackInfo ci) {
        if (!this.fullscreen) {
            if (!FullscreenPlusFabricCaching.cachedPos) {
                FullscreenPlusConstants.LOGGER.info("Window position has been cached");
            }
            FullscreenPlusFabricCaching.cachedPos = true;
            FullscreenPlusFabricCaching.cachedX = x;
            FullscreenPlusFabricCaching.cachedY = y;
        }
    }

    @Inject(method = "onFramebufferSizeChanged", at = @At("HEAD"))
    private void inject$onFramebufferSizeChanged$head(long window, int width, int height, CallbackInfo ci) {
        if (!this.fullscreen && !FullscreenPlusFabricCaching.cacheSizeLock) {
            if (!FullscreenPlusFabricCaching.cachedSize) {
                FullscreenPlusConstants.LOGGER.info("Window size has been cached");
            }
            FullscreenPlusFabricCaching.cachedSize = true;
            FullscreenPlusFabricCaching.cachedWidth = width;
            FullscreenPlusFabricCaching.cachedHeight = height;
        }
    }

}
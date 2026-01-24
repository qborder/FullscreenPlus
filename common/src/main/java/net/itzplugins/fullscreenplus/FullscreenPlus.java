package net.itzplugins.fullscreenplus;

import net.itzplugins.fullscreenplus.mixin.accessor.WindowAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;

public class FullscreenPlus {

    public static void init() {
        FullscreenPlusConstants.LOGGER.info("Welcome to borderless world! Enjoy enhanced fullscreen!");
    }

    public static void toggleFullScreenMode(GameOptions options, boolean value) {
        options.getFullscreen().setValue(value);

        Window window = MinecraftClient.getInstance().getWindow();

        if (window.isFullscreen() != options.getFullscreen().getValue()) {
            window.toggleFullscreen();
            options.getFullscreen().setValue(window.isFullscreen());
        }

        if (options.getFullscreen().getValue()) {
            ((WindowAccessor) (Object) window).setFullscreenVideoModeDirty(true);
        }
    }

}

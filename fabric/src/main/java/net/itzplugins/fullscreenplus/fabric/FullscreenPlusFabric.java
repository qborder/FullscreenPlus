package net.itzplugins.fullscreenplus.fabric;

import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Main class for fabric version of Concentration
 *
 * @author DeeChael
 */
public class FullscreenPlusFabric implements ClientModInitializer {

    private static KeyBinding cycleModesKey;

    @Override
    public void onInitializeClient() {
        FullscreenPlus.init();

        cycleModesKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "fullscreenplus.key.cycle_modes",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                KeyBinding.Category.create(Identifier.of("fullscreenplus", "fullscreenplus"))
        ));

        if (System.getProperty("fullscreenplus.borderless") != null) {
            FullscreenPlusConstants.LOGGER.info("--borderless flag detected, forcing borderless mode");
            FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();
            config.fullscreen = FullscreenMode.BORDERLESS;
            config.save();
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (cycleModesKey.wasPressed()) {
                FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();
                if (!config.cycleModesKeybind) return;

                FullscreenMode current = config.fullscreen;
                FullscreenMode next = switch (current) {
                    case BORDERLESS -> FullscreenMode.EXCLUSIVE_BORDERLESS;
                    case EXCLUSIVE_BORDERLESS -> FullscreenMode.NATIVE;
                    case NATIVE -> FullscreenMode.BORDERLESS;
                };
                config.fullscreen = next;
                config.save();
                FullscreenPlusConstants.LOGGER.info("Cycled fullscreen mode: {} -> {}", current, next);

                if (client.options.getFullscreen().getValue()) {
                    FullscreenPlus.toggleFullScreenMode(client.options, true);
                }
            }
        });
    }

}

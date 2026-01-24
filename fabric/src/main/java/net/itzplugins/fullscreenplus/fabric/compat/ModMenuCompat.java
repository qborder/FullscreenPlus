package net.itzplugins.fullscreenplus.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.FullscreenPlusConstants;
import net.itzplugins.fullscreenplus.config.FullscreenPlusConfigScreen;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.Arrays;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            FullscreenPlusConfigFabric configHandler = FullscreenPlusConfigFabric.getInstance();

            return new FullscreenPlusConfigScreen(Text.literal(FullscreenPlusConstants.MOD_NAME), parent) {
                @Override
                public void save() {
                    configHandler.save();
                    FullscreenPlus.toggleFullScreenMode(MinecraftClient.getInstance().options, MinecraftClient.getInstance().options.getFullscreen().getValue());
                }

                @Override
                public void addElements() {
                    addHeading(Text.translatable("fullscreenplus.config.heading.mode"));
                    
                    addOption(new SimpleOption<>(
                            "fullscreenplus.option.fullscreen_mode",
                            SimpleOption.emptyTooltip(),
                            (text, mode) -> mode.getCaption(),
                            new SimpleOption.PotentialValuesBasedCallbacks<>(Arrays.asList(FullscreenMode.values()), FullscreenMode.CODEC),
                            configHandler.fullscreen,
                            value -> configHandler.fullscreen = value
                    ));

                    addHeading(Text.translatable("fullscreenplus.config.heading.customization"));
                    
                    addOption(SimpleOption.ofBoolean("fullscreenplus.config.customization.enabled",
                            configHandler.customized,
                            value -> configHandler.customized = value));
                    addOption(SimpleOption.ofBoolean("fullscreenplus.config.customization.related",
                            configHandler.related,
                            value -> configHandler.related = value));

                    addIntField(Text.translatable("fullscreenplus.config.customization.x"),
                            () -> configHandler.x,
                            value -> configHandler.x = value);
                    addIntField(Text.translatable("fullscreenplus.config.customization.y"),
                            () -> configHandler.y,
                            value -> configHandler.y = value);
                    addIntField(Text.translatable("fullscreenplus.config.customization.width"),
                            () -> configHandler.width,
                            value -> configHandler.width = value > 0 ? value : 1);
                    addIntField(Text.translatable("fullscreenplus.config.customization.height"),
                            () -> configHandler.height,
                            value -> configHandler.height = value > 0 ? value : 1);
                }
            };
        };
    }

}

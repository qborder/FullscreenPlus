package net.itzplugins.fullscreenplus.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlus;
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
            FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

            return new FullscreenPlusConfigScreen(Text.translatable("fullscreenplus.name"), parent) {
                @Override
                public void save() {
                    config.save();
                    if (MinecraftClient.getInstance().getWindow() != null) {
                        FullscreenPlus.toggleFullScreenMode(
                                MinecraftClient.getInstance().options,
                                MinecraftClient.getInstance().options.getFullscreen().getValue()
                        );
                    }
                }

                @Override
                public void addElements() {
                    addHeading(Text.translatable("fullscreenplus.config.heading.mode"));

                    addOption(new SimpleOption<>(
                            "fullscreenplus.option.fullscreen_mode",
                            SimpleOption.emptyTooltip(),
                            (text, mode) -> mode.getCaption(),
                            new SimpleOption.PotentialValuesBasedCallbacks<>(
                                    Arrays.asList(FullscreenMode.values()),
                                    FullscreenMode.CODEC
                            ),
                            config.fullscreen,
                            value -> config.fullscreen = value
                    ));

                    addHeading(Text.translatable("fullscreenplus.config.heading.customization"));

                    addBooleanOption(
                            Text.translatable("fullscreenplus.config.customization.enabled"),
                            () -> config.customized,
                            value -> config.customized = value
                    );

                    addBooleanOption(
                            Text.translatable("fullscreenplus.config.customization.related"),
                            () -> config.related,
                            value -> config.related = value
                    );

                    addIntField(
                            Text.translatable("fullscreenplus.config.customization.x"),
                            () -> config.x,
                            value -> config.x = value
                    );

                    addIntField(
                            Text.translatable("fullscreenplus.config.customization.y"),
                            () -> config.y,
                            value -> config.y = value
                    );

                    addIntField(
                            Text.translatable("fullscreenplus.config.customization.width"),
                            () -> config.width,
                            value -> config.width = Math.max(1, value)
                    );

                    addIntField(
                            Text.translatable("fullscreenplus.config.customization.height"),
                            () -> config.height,
                            value -> config.height = Math.max(1, value)
                    );
                }
            };
        };
    }
}

package net.itzplugins.fullscreenplus.fabric.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.FullscreenPlusNotifications;
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
                                                                MinecraftClient.getInstance().options.getFullscreen()
                                                                                .getValue());
                                        }
                                        if (config.showNotifications) {
                                                FullscreenPlusNotifications.showSettingsSaved();
                                        }
                                }

                                @Override
                                public void addElements() {
                                        addHeading(Text.translatable("fullscreenplus.config.heading.mode"));

                                        addOption(new SimpleOption<>(
                                                        "fullscreenplus.option.fullscreen_mode",
                                                        SimpleOption.constantTooltip(Text.translatable(
                                                                        "fullscreenplus.option.fullscreen_mode.tooltip")),
                                                        (text, mode) -> mode.getCaption(),
                                                        new SimpleOption.PotentialValuesBasedCallbacks<>(
                                                                        Arrays.asList(FullscreenMode.values()),
                                                                        FullscreenMode.CODEC),
                                                        config.fullscreen,
                                                        value -> config.fullscreen = value));

                                        addHeading(Text.translatable("fullscreenplus.config.heading.performance"));

                                        addBooleanOption(
                                                        Text.translatable("fullscreenplus.config.dynamic_resolution"),
                                                        () -> config.dynamicResolution,
                                                        value -> config.dynamicResolution = value);

                                        addIntField(
                                                        Text.translatable("fullscreenplus.config.render_scale"),
                                                        () -> config.renderScale,
                                                        value -> config.renderScale = Math.max(25,
                                                                        Math.min(100, value)));

                                        addBooleanOption(
                                                        Text.translatable("fullscreenplus.config.low_latency"),
                                                        () -> config.lowLatencyMode,
                                                        value -> config.lowLatencyMode = value);

                                        addHeading(Text.translatable("fullscreenplus.config.heading.window"));

                                        addBooleanOption(
                                                        Text.translatable("fullscreenplus.config.windowed_borderless"),
                                                        () -> config.windowedBorderless,
                                                        value -> config.windowedBorderless = value);

                                        addIntField(
                                                        Text.translatable(
                                                                        "fullscreenplus.config.windowed_borderless_width"),
                                                        () -> config.windowedBorderlessWidth,
                                                        value -> config.windowedBorderlessWidth = Math.max(640, value));

                                        addIntField(
                                                        Text.translatable(
                                                                        "fullscreenplus.config.windowed_borderless_height"),
                                                        () -> config.windowedBorderlessHeight,
                                                        value -> config.windowedBorderlessHeight = Math.max(480,
                                                                        value));

                                        addStringField(
                                                        Text.translatable("fullscreenplus.config.window_title"),
                                                        () -> config.windowTitle,
                                                        value -> config.windowTitle = value);

                                        addHeading(Text.translatable("fullscreenplus.config.heading.advanced"));

                                        addBooleanOption(
                                                        Text.translatable("fullscreenplus.config.ultrawide_support"),
                                                        () -> config.ultrawideSupport,
                                                        value -> config.ultrawideSupport = value);

                                        addBooleanOption(
                                                        Text.translatable("fullscreenplus.config.hdr_passthrough"),
                                                        () -> config.hdrPassthrough,
                                                        value -> config.hdrPassthrough = value);

                                        addBooleanOption(
                                                        Text.translatable(
                                                                        "fullscreenplus.config.refresh_rate_override"),
                                                        () -> config.refreshRateOverride,
                                                        value -> config.refreshRateOverride = value);

                                        addIntField(
                                                        Text.translatable("fullscreenplus.config.refresh_rate"),
                                                        () -> config.refreshRate,
                                                        value -> config.refreshRate = Math.max(30,
                                                                        Math.min(360, value)));

                                        addBooleanOption(
                                                        Text.translatable("fullscreenplus.config.show_notifications"),
                                                        () -> config.showNotifications,
                                                        value -> config.showNotifications = value);

                                        addHeading(Text.translatable("fullscreenplus.config.heading.customization"));

                                        addBooleanOption(
                                                        Text.translatable(
                                                                        "fullscreenplus.config.customization.enabled"),
                                                        () -> config.customized,
                                                        value -> config.customized = value);

                                        addBooleanOption(
                                                        Text.translatable(
                                                                        "fullscreenplus.config.customization.related"),
                                                        () -> config.related,
                                                        value -> config.related = value);

                                        addIntField(
                                                        Text.translatable("fullscreenplus.config.customization.x"),
                                                        () -> config.x,
                                                        value -> config.x = value);

                                        addIntField(
                                                        Text.translatable("fullscreenplus.config.customization.y"),
                                                        () -> config.y,
                                                        value -> config.y = value);

                                        addIntField(
                                                        Text.translatable("fullscreenplus.config.customization.width"),
                                                        () -> config.width,
                                                        value -> config.width = Math.max(1, value));

                                        addIntField(
                                                        Text.translatable("fullscreenplus.config.customization.height"),
                                                        () -> config.height,
                                                        value -> config.height = Math.max(1, value));
                                }
                        };
                };
        }
}

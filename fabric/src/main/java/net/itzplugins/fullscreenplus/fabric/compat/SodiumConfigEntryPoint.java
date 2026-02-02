package net.itzplugins.fullscreenplus.fabric.compat;

import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.itzplugins.fullscreenplus.FullscreenMode;
import net.itzplugins.fullscreenplus.FullscreenPlus;
import net.itzplugins.fullscreenplus.fabric.config.FullscreenPlusConfigFabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SodiumConfigEntryPoint implements ConfigEntryPoint {

        private void onStorageChange() {
                FullscreenPlusConfigFabric.getInstance().save();
                if (MinecraftClient.getInstance().options.getFullscreen().getValue()) {
                        FullscreenPlus.toggleFullScreenMode(MinecraftClient.getInstance().options, true);
                }
        }

        @Override
        public void registerConfigLate(ConfigBuilder builder) {
                FullscreenPlusConfigFabric config = FullscreenPlusConfigFabric.getInstance();

                builder.registerOwnModOptions()
                                .setIcon(Identifier.of("fullscreenplus", "textures/gui/icon.png"))
                                .addPage(builder.createOptionPage()
                                                .setName(Text.translatable("fullscreenplus.options.title"))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Text.translatable(
                                                                                "fullscreenplus.options.group.display"))
                                                                .addOption(builder.createEnumOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "fullscreen_mode"),
                                                                                FullscreenMode.class)
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.option.fullscreen_mode"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.option.fullscreen_mode.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.fullscreen = value,
                                                                                                () -> config.fullscreen)
                                                                                .setDefaultValue(
                                                                                                FullscreenMode.BORDERLESS)
                                                                                .setElementNameProvider(
                                                                                                FullscreenMode::getCaption)))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Text.translatable(
                                                                                "fullscreenplus.options.group.performance"))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "dynamic_resolution"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.dynamic_resolution"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.dynamic_resolution.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.dynamicResolution = value,
                                                                                                () -> config.dynamicResolution)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "low_latency"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.low_latency"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.low_latency.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.lowLatencyMode = value,
                                                                                                () -> config.lowLatencyMode)
                                                                                .setDefaultValue(false)))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Text.translatable(
                                                                                "fullscreenplus.options.group.window"))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "windowed_borderless"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.windowed_borderless"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.windowed_borderless.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.windowedBorderless = value,
                                                                                                () -> config.windowedBorderless)
                                                                                .setDefaultValue(false)))
                                                .addOptionGroup(builder.createOptionGroup()
                                                                .setName(Text.translatable(
                                                                                "fullscreenplus.options.group.advanced"))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "ultrawide_support"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.ultrawide_support"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.ultrawide_support.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.ultrawideSupport = value,
                                                                                                () -> config.ultrawideSupport)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "hdr_passthrough"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.hdr_passthrough"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.hdr_passthrough.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.hdrPassthrough = value,
                                                                                                () -> config.hdrPassthrough)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "refresh_rate_override"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.refresh_rate_override"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.refresh_rate_override.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.refreshRateOverride = value,
                                                                                                () -> config.refreshRateOverride)
                                                                                .setDefaultValue(false))
                                                                .addOption(builder.createBooleanOption(
                                                                                Identifier.of("fullscreenplus",
                                                                                                "show_notifications"))
                                                                                .setName(Text.translatable(
                                                                                                "fullscreenplus.config.show_notifications"))
                                                                                .setTooltip(Text.translatable(
                                                                                                "fullscreenplus.config.show_notifications.tooltip"))
                                                                                .setStorageHandler(
                                                                                                this::onStorageChange)
                                                                                .setBinding(
                                                                                                value -> config.showNotifications = value,
                                                                                                () -> config.showNotifications)
                                                                                .setDefaultValue(true))));
        }
}